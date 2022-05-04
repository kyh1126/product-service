package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.apiclient.OrderManagementServiceApiClient
import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.request.OutboundCancelModel
import com.smartfoodnet.apiclient.request.TrackingDataModel
import com.smartfoodnet.apiclient.request.TrackingNumberRegisterModel
import com.smartfoodnet.apiclient.request.TrackingOptionModel
import com.smartfoodnet.apiclient.response.*
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.error.exception.ErrorCode
import com.smartfoodnet.common.getNosnosErrorMessage
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.entity.ReleaseProduct
import com.smartfoodnet.fnproduct.release.model.dto.OrderReleaseInfoDto
import com.smartfoodnet.fnproduct.release.model.dto.ReleaseModelDto
import com.smartfoodnet.fnproduct.release.model.vo.DeliveryAgency
import com.smartfoodnet.fnproduct.release.model.vo.PausedBy
import com.smartfoodnet.fnproduct.release.model.vo.ReleaseStatus
import com.smartfoodnet.fnproduct.release.model.vo.TrackingNumberStatus
import feign.FeignException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ReleaseInfoStoreService(
    private val releaseInfoRepository: ReleaseInfoRepository,
    private val orderManagementServiceApiClient: OrderManagementServiceApiClient,
    private val wmsApiClient: WmsApiClient,
) {
    /**
     * 노스노스에서 응답 받은 데이터로 ReleaseInfo 엔티티를 생성하는 함수
     */
    @Transactional
    fun createFromOrderInfo(partnerId: Long, orderInfo: PostOutboundModel) {
        releaseInfoRepository.save(orderInfo.toReleaseInfo(partnerId))
    }

    /**
     * OrderId 별 트랜잭션 시작
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun createOrUpdateReleaseInfo(
        releaseModels: List<NosnosReleaseModel>,
        itemModelsByReleaseId: Map<Long, List<NosnosReleaseItemModel>>,
        basicProductByShippingProductId: Map<Long, BasicProduct>,
        orderReleaseInfoDto: OrderReleaseInfoDto
    ) {
        val (confirmOrder, targetReleaseInfoList) = orderReleaseInfoDto

        releaseModels.map { model ->
            val releaseInfoByReleaseId = targetReleaseInfoList.associateBy { it.releaseId }
            val releaseId = model.releaseId!!
            val releaseItemModels = itemModelsByReleaseId[releaseId] ?: emptyList()
            val releaseModelDto = ReleaseModelDto(model, releaseItemModels)

            when {
                // Case1: releaseInfo 테이블 내 releaseId 가 있는 경우
                isExistingReleaseId(releaseId, releaseInfoByReleaseId) ->
                    updateExistingReleaseId(
                        releaseId = releaseId,
                        releaseModelDto = releaseModelDto,
                        basicProductByShippingProductId = basicProductByShippingProductId
                    )
                // Case2: releaseId 가 null 인 releaseInfo 업데이트
                isNeedToBeUpdatedReleaseId(releaseInfoByReleaseId) -> {
                    val targetReleaseInfo = releaseInfoByReleaseId[null]!!
                    updateReleaseId(
                        targetReleaseInfo.id!!,
                        releaseModelDto,
                        basicProductByShippingProductId
                    )
                    targetReleaseInfo.releaseId = releaseId
                }
                // Case3: releaseInfo 엔티티 생성
                else -> {
                    val firstTargetReleaseInfo = targetReleaseInfoList.first()
                    createReleaseInfo(
                        releaseModelDto,
                        basicProductByShippingProductId,
                        firstTargetReleaseInfo,
                        confirmOrder
                    )
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateDeliveryCompletedAt(
        targetIds: Collection<Long>,
        lotteDeliveryInfoByTrackingNumber: Map<String, LotteDeliveryInfoDetail> = emptyMap(),
        cjDeliveryInfoByTrackingNumber: Map<String, CjDeliveryStatusModel> = emptyMap(),
    ) {
        releaseInfoRepository.findAllById(targetIds).forEach { releaseInfo ->
            when {
                lotteDeliveryInfoByTrackingNumber.isNotEmpty() -> {
                    lotteDeliveryInfoByTrackingNumber[releaseInfo.trackingNumber]?.let {
                        releaseInfo.updateDeliveryCompletedAt(it.procDateTime)
                    }
                }
                cjDeliveryInfoByTrackingNumber.isNotEmpty() -> {
                    cjDeliveryInfoByTrackingNumber[releaseInfo.trackingNumber]?.let {
                        releaseInfo.updateDeliveryCompletedAt(LocalDateTime.now())
                    }
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateTrackingNumberStatus(ids: List<Long>, deliveryAgencyById: Map<Long, DeliveryAgency?>) {
        val targetList = releaseInfoRepository.findAllById(ids)

        val targetMap = targetList
            .flatMap { TrackingNumberRegisterModel.fromEntity(it, deliveryAgencyById) }
            .distinctBy { it.orderNumber }
            .groupBy { Pair(it.partnerId, it.storeCode!!) }

        targetMap.entries.forEach { (key, models) ->
            val (partnerId, storeCode) = key
            val trackingOptionModel =
                TrackingOptionModel(dataModelList = models.map(TrackingDataModel::fromModel))

            orderManagementServiceApiClient.sendTrackingNumber(
                partnerId,
                storeCode,
                trackingOptionModel
            )
        }

        targetList.forEach {
            it.updateTrackingNumberStatus(TrackingNumberStatus.WAITING_CALLBACK)
        }
    }

    fun pauseReleaseInfo(id: Long) {
        val releaseInfo = releaseInfoRepository.findById(id).get()
        if (releaseInfo.releaseStatus != ReleaseStatus.BEFORE_RELEASE_REQUEST) {
            throw BaseRuntimeException(errorMessage = "출고요청전 상태인 경우만 출고중지가 가능합니다.")
        }

        try {
            wmsApiClient.cancelOutbound(OutboundCancelModel.fromEntity(releaseInfo))
        } catch (e: FeignException) {
            log.error("[pauseReleaseInfo] ${getNosnosErrorMessage(e.message)}", e)
            throw e
        }
        releaseInfo.pause(PausedBy.PARTNER)
    }

    fun cancelReleaseInfo(id: Long) {
        val releaseInfo = releaseInfoRepository.findById(id).get()
        if (releaseInfo.releaseStatus != ReleaseStatus.RELEASE_PAUSED) {
            throw BaseRuntimeException(errorMessage = "출고중지 상태인 경우만 출고 철회가 가능합니다. id: ${id}")
        }

        releaseInfo.cancel()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun processReOrderResult(id: Long, nextOrderCode: String) {
        val releaseInfo = releaseInfoRepository.findById(id).get()

        releaseInfo.processNextOrderCode(nextOrderCode)

        releaseInfoRepository.findByOrderCode(nextOrderCode).forEach { nextReleaseInfo ->
            nextReleaseInfo.linkPreviousCodes(releaseInfo.orderCode, releaseInfo.releaseCode)
        }
    }

    private fun isNeedToBeUpdatedReleaseId(releaseInfoByReleaseId: Map<Long?, ReleaseInfo>) =
        releaseInfoByReleaseId.containsKey(null)

    private fun isExistingReleaseId(
        releaseId: Long,
        releaseInfoByReleaseId: Map<Long?, ReleaseInfo>
    ) = releaseId in releaseInfoByReleaseId.keys

    private fun getUploadType(targetReleaseInfo: ReleaseInfo) =
        targetReleaseInfo.confirmOrder?.requestOrderList?.firstOrNull()?.collectedOrder?.uploadType
            ?: OrderUploadType.API

    private fun updateReleaseId(
        releaseInfoId: Long,
        releaseModelDto: ReleaseModelDto,
        basicProductByShippingProductId: Map<Long, BasicProduct>
    ) {
        val targetReleaseInfo = releaseInfoRepository.findById(releaseInfoId).get()
        targetReleaseInfo.updateReleaseId(releaseModelDto.releaseModel)

        updateExistingReleaseId(
            targetReleaseInfo.releaseId!!,
            targetReleaseInfo,
            releaseModelDto,
            basicProductByShippingProductId
        )
    }

    private fun updateExistingReleaseId(
        releaseId: Long,
        releaseInfoEntity: ReleaseInfo? = null,
        releaseModelDto: ReleaseModelDto,
        basicProductByShippingProductId: Map<Long, BasicProduct>
    ) {
        val (releaseModel, releaseItemModels) = releaseModelDto

        val targetReleaseInfo = releaseInfoEntity
            ?: (releaseInfoRepository.findByReleaseId(releaseId) ?: return)

        // 릴리즈상품 저장
        val entityByReleaseItemId =
            targetReleaseInfo.releaseProducts.associateBy { it.releaseItemId }
        val releaseProducts = createOrUpdateReleaseProducts(
            releaseItemModels,
            entityByReleaseItemId,
            basicProductByShippingProductId
        )

        targetReleaseInfo.update(releaseModel, releaseProducts, getUploadType(targetReleaseInfo))
    }

    private fun createReleaseInfo(
        releaseModelDto: ReleaseModelDto,
        basicProductByShippingProductId: Map<Long, BasicProduct>,
        firstTargetReleaseInfo: ReleaseInfo,
        confirmOrder: ConfirmOrder,
    ) {
        val (releaseModel, releaseItemModels) = releaseModelDto

        // 릴리즈상품 저장
        val releaseProducts = createOrUpdateReleaseProducts(
            releaseItemModels = releaseItemModels,
            basicProductByShippingProductId = basicProductByShippingProductId
        )

        val uploadType = getUploadType(firstTargetReleaseInfo)

        val releaseInfo = releaseModel.toEntity(releaseProducts, uploadType, confirmOrder)
        releaseInfoRepository.save(releaseInfo)
    }

    private fun createOrUpdateReleaseProducts(
        releaseItemModels: List<NosnosReleaseItemModel>,
        entityByReleaseItemId: Map<Long, ReleaseProduct> = emptyMap(),
        basicProductByShippingProductId: Map<Long, BasicProduct>,
    ): Set<ReleaseProduct> {
        val releaseProducts = releaseItemModels.map {
            val basicProduct = basicProductByShippingProductId[it.shippingProductId!!]
                ?: throw BaseRuntimeException(errorCode = ErrorCode.NO_ELEMENT)
            val releaseItemId = it.releaseItemId!!
            if (releaseItemId !in entityByReleaseItemId.keys) it.toEntity(basicProduct)
            else {
                val entity = entityByReleaseItemId[releaseItemId]
                entity!!.update(it.quantity ?: 0)
                entity
            }
        }.run { LinkedHashSet(this) }

        // 연관관계 끊긴 entity 삭제처리
        entityByReleaseItemId.values.toSet().minus(releaseProducts)
            .forEach(ReleaseProduct::delete)

        return releaseProducts
    }

    companion object : Log
}
