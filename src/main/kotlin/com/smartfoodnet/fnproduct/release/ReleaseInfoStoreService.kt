package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.apiclient.response.DeliveryInfoDetail
import com.smartfoodnet.apiclient.response.NosnosReleaseItemModel
import com.smartfoodnet.apiclient.response.NosnosReleaseModel
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.error.exception.ErrorCode
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.entity.ReleaseProduct
import com.smartfoodnet.fnproduct.release.model.dto.ReleaseModelDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ReleaseInfoStoreService(
    private val releaseInfoRepository: ReleaseInfoRepository,
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun createOrUpdateReleaseInfo(
        releaseModels: List<NosnosReleaseModel>,
        itemModelsByReleaseId: Map<Long, List<NosnosReleaseItemModel>>,
        basicProductByShippingProductId: Map<Long, BasicProduct>,
        targetReleaseInfoList: List<ReleaseInfo>
    ) {
        val releaseInfoByReleaseId = targetReleaseInfoList.associateBy { it.releaseId }

        releaseModels.map { model ->
            val releaseId = model.releaseId!!.toLong()
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
                isNeedToBeUpdatedReleaseId(targetReleaseInfoList, releaseModels) -> {
                    val targetReleaseInfoId = releaseInfoByReleaseId[null]!!.id!!
                    updateReleaseId(
                        targetReleaseInfoId,
                        releaseModelDto,
                        basicProductByShippingProductId
                    )
                }
                // Case3: releaseInfo 엔티티 생성
                else -> {
                    val uploadType = getUploadType(targetReleaseInfoList.first())
                    createReleaseInfo(releaseModelDto, basicProductByShippingProductId, uploadType)
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateDeliveryCompletedAt(
        targetIds: Collection<Long>,
        deliveryInfoByShippingCode: Map<String, DeliveryInfoDetail>
    ) {
        releaseInfoRepository.findAllById(targetIds).forEach { releaseInfo ->
            deliveryInfoByShippingCode[releaseInfo.shippingCode]?.let {
                releaseInfo.updateDeliveryCompletedAt(it.procDateTime)
            }
        }
    }

    private fun isNeedToBeUpdatedReleaseId(
        targetReleaseInfoList: List<ReleaseInfo>,
        releaseModels: List<NosnosReleaseModel>
    ) = targetReleaseInfoList.size == releaseModels.size

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
        uploadType: OrderUploadType
    ) {
        val (releaseModel, releaseItemModels) = releaseModelDto

        // 릴리즈상품 저장
        val releaseProducts = createOrUpdateReleaseProducts(
            releaseItemModels = releaseItemModels,
            basicProductByShippingProductId = basicProductByShippingProductId
        )

        val releaseInfo = releaseModel.toEntity(releaseProducts, uploadType)
        releaseInfoRepository.save(releaseInfo)
    }

    private fun createOrUpdateReleaseProducts(
        releaseItemModels: List<NosnosReleaseItemModel>,
        entityByReleaseItemId: Map<Long, ReleaseProduct> = emptyMap(),
        basicProductByShippingProductId: Map<Long, BasicProduct>,
    ): Set<ReleaseProduct> {
        val releaseProducts = releaseItemModels.map {
            val basicProduct = basicProductByShippingProductId[it.shippingProductId!!.toLong()]
                ?: throw BaseRuntimeException(errorCode = ErrorCode.NO_ELEMENT)
            val releaseItemId = it.releaseItemId!!.toLong()
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
}
