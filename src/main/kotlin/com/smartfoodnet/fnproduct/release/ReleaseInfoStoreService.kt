package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.apiclient.OrderManagementServiceApiClient
import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.request.*
import com.smartfoodnet.apiclient.response.*
import com.smartfoodnet.apiclient.response.GetOutboundCancelModel.CancelledOutboundModel
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
    fun createFromOrderInfo(partnerId: Long, orderInfo: PostOutboundModel, confirmOrder: ConfirmOrder) {
        releaseInfoRepository.save(orderInfo.toReleaseInfo(partnerId, confirmOrder))
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

        val processedReleaseInfoList = releaseModels.mapNotNull { model ->
            val releaseInfoByReleaseId = targetReleaseInfoList.associateBy { it.releaseId }
            val releaseId = model.releaseId!!
            val releaseItemModels = itemModelsByReleaseId[releaseId] ?: emptyList()
            val releaseModelDto = ReleaseModelDto(model, releaseItemModels, confirmOrder)

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
                    targetReleaseInfo.releaseId = releaseId
                    updateReleaseId(
                        targetReleaseInfo.id!!,
                        releaseModelDto,
                        basicProductByShippingProductId
                    )
                }
                // Case3: releaseInfo 엔티티 생성
                else -> {
                    val firstTargetReleaseInfo = targetReleaseInfoList.first()
                    createReleaseInfo(
                        releaseModelDto,
                        basicProductByShippingProductId,
                        firstTargetReleaseInfo
                    )
                }
            }
        }

        processPausedReleaseInfo(processedReleaseInfoList)
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
            orderManagementServiceApiClient.sendTrackingNumber(
                partnerId,
                storeCode,
                TrackingOptionModel(models.map(TrackingDataModel::fromModel))
            )
        }

        targetList.forEach {
            it.updateTrackingNumberStatus(TrackingNumberStatus.WAITING_CALLBACK)
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun processReOrderResult(id: Long, nextOrderCode: String) {
        val releaseInfo = getReleaseInfo(id)

        releaseInfo.processNextOrderCode(nextOrderCode)

        releaseInfoRepository.findByOrderCode(nextOrderCode).forEach { nextReleaseInfo ->
            nextReleaseInfo.linkPreviousCodes(releaseInfo.orderCode, releaseInfo.releaseCode)
        }
    }

    fun pauseReleaseInfo(id: Long) {
        val releaseInfo = getReleaseInfo(id)
        if (releaseInfo.releaseStatus != ReleaseStatus.BEFORE_RELEASE_REQUEST) {
            throw BaseRuntimeException(errorMessage = "출고요청전 상태인 경우만 출고중지가 가능합니다.")
        }

        try {
            wmsApiClient.cancelOutbound(OutboundCancelCreateModel.fromEntity(releaseInfo))
        } catch (e: FeignException) {
            log.error("[pauseReleaseInfo] ${getNosnosErrorMessage(e.message)}", e)
            throw e
        }
        releaseInfo.pause(pausedBy = PausedBy.PARTNER)
    }

    fun cancelReleaseInfo(id: Long) {
        val releaseInfo = getReleaseInfo(id)
        if (releaseInfo.releaseStatus != ReleaseStatus.RELEASE_PAUSED) {
            throw BaseRuntimeException(errorMessage = "출고중지 상태인 경우만 출고 철회가 가능합니다. id: ${id}")
        }

        releaseInfo.cancel()
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
    ): ReleaseInfo? {
        val targetReleaseInfo = getReleaseInfo(releaseInfoId)
        targetReleaseInfo.updateReleaseId(releaseModelDto.releaseModel)

        return updateExistingReleaseId(
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
    ): ReleaseInfo? {
        val (releaseModel, releaseItemModels, confirmOrder) = releaseModelDto

        val targetReleaseInfo = releaseInfoEntity
            ?: (releaseInfoRepository.findByReleaseId(releaseId) ?: return null)

        // 릴리즈상품 저장
        val entityByReleaseItemId =
            targetReleaseInfo.releaseProducts.associateBy { it.releaseItemId }
        val releaseProducts = createOrUpdateReleaseProducts(
            releaseItemModels,
            entityByReleaseItemId,
            basicProductByShippingProductId
        )

        targetReleaseInfo.update(releaseModel, releaseProducts, getUploadType(targetReleaseInfo), confirmOrder.shippingMethodType)
        return targetReleaseInfo
    }

    private fun createReleaseInfo(
        releaseModelDto: ReleaseModelDto,
        basicProductByShippingProductId: Map<Long, BasicProduct>,
        firstTargetReleaseInfo: ReleaseInfo,
    ): ReleaseInfo {
        val (releaseModel, releaseItemModels, confirmOrder) = releaseModelDto

        // 릴리즈상품 저장
        val releaseProducts = createOrUpdateReleaseProducts(
            releaseItemModels = releaseItemModels,
            basicProductByShippingProductId = basicProductByShippingProductId
        )

        val uploadType = getUploadType(firstTargetReleaseInfo)

        val releaseInfo = releaseModel.toEntity(releaseProducts, uploadType, confirmOrder)
        return releaseInfoRepository.save(releaseInfo)
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

    /**
     * OrderId 별 ReleaseInfo 중 출고중지 상태인 경우 후처리
     */
    private fun processPausedReleaseInfo(processedList: List<ReleaseInfo>) {
        val targetList = processedList
            .filter { it.releaseStatus == ReleaseStatus.RELEASE_PAUSED }
        val firstTarget = targetList.firstOrNull() ?: return

        val cancelOutboundMap = getCancelOutboundMap(firstTarget)

        targetList.forEach { pausedReleaseInfo ->
            val searchCondition = Pair(pausedReleaseInfo.orderId, pausedReleaseInfo.releaseId)
            pausedReleaseInfo.processNosnosPause(cancelOutboundMap[searchCondition])
        }
    }

    private fun getCancelOutboundMap(firstTarget: ReleaseInfo): Map<Pair<Long, Long?>, CancelledOutboundModel> {
        var cancelOutboundsBy: Map<Pair<Long, Long?>, CancelledOutboundModel> = mapOf()
        try {
            val cancelOutbounds = wmsApiClient.getCancelOutbounds(
                OutboundCancelReadModel(partnerId = firstTarget.partnerId, orderId = firstTarget.orderId)
            ).payload?.dataList ?: emptyList()

            cancelOutboundsBy = cancelOutbounds.map(GetOutboundCancelModel::toCancelledOutboundModel)
                .associateBy { Pair(it.orderId, it.releaseId) }
        } catch (e: FeignException) {
            // nosnos 출고 메뉴에서 바로 출고취소 클릭시 발주 취소요청은 생성되지 않는다. (known issue)
            log.warn("[processPausedReleaseInfo] ${getNosnosErrorMessage(e.message)}", e)
        }
        return cancelOutboundsBy
    }

    private fun getReleaseInfo(id: Long) = releaseInfoRepository.findById(id).get()

    companion object : Log
}
