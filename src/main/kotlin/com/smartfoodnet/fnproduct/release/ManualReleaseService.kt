package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.common.error.SaveState
import com.smartfoodnet.common.error.ValidatorUtils
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.model.header.SfnMetaUser
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fnpartner.PartnerService
import com.smartfoodnet.fnproduct.order.ConfirmOrderService
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.entity.ConfirmProduct
import com.smartfoodnet.fnproduct.order.model.RequestOrderCreateModel
import com.smartfoodnet.fnproduct.order.model.response.ManualOrderResponseModel
import com.smartfoodnet.fnproduct.order.support.CollectedOrderRepository
import com.smartfoodnet.fnproduct.order.support.ConfirmProductRepository
import com.smartfoodnet.fnproduct.order.vo.MatchingType
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.release.model.request.ManualOrderModel
import com.smartfoodnet.fnproduct.release.model.request.ManualProductModel
import com.smartfoodnet.fnproduct.release.model.request.ManualReleaseCreateModel
import com.smartfoodnet.fnproduct.release.model.request.ReOrderCreateModel
import com.smartfoodnet.fnproduct.release.validator.ManualOrderModelValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * 주문외출고를 위한 서비스
 */
@Service
@Transactional(readOnly = true)
class ManualReleaseService(
    private val manualOrderModelValidator: ManualOrderModelValidator,
    private val collectedOrderRepository: CollectedOrderRepository,
    private val basicProductRepository: BasicProductRepository,
    private val confirmProductRepository: ConfirmProductRepository,
    private val confirmOrderService: ConfirmOrderService,
    private val partnerService: PartnerService,
    private val releaseInfoStoreService: ReleaseInfoStoreService
) {
    /**
     * 주문외출고 등록(상품과 주문 정보 없이 주문수집 데이터를 생성한다)
     * - Collected Order생성(쇼핑몰 주문하고 쇼핑몰 상품 없이 등록
     *   uploadType: [com.smartfoodnet.fnproduct.order.vo.OrderUploadType.MANUAL])
     *
     * - Collected Order생성 > Confirm Product생성 > Confirm Order생성(노스노스 발주 등록/SFN출고지시)
     */
    @Transactional
    fun issueManualRelease(
        sfnMetaUser: SfnMetaUser?,
        partnerId: Long,
        manualReleaseRequest: ManualReleaseCreateModel
    ): ManualOrderResponseModel {
        // validation
        ValidatorUtils.validateAndThrow(
            SaveState.CREATE,
            manualOrderModelValidator,
            manualReleaseRequest
        )

        partnerService.checkUserPartnerMembership(sfnMetaUser, partnerId)
        return createManualOrder(partnerId, manualReleaseRequest)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun reOrder(releaseInfoId: Long, partnerId: Long, createModel: ReOrderCreateModel) {
        val reOrderResponseModel = issueReOrder(partnerId, createModel)
        val nextOrderCode = reOrderResponseModel.confirmOrder?.nosnosOrderCode!!

        releaseInfoStoreService.processReOrderResult(releaseInfoId, nextOrderCode)
    }

    /**
     * 재출고 등록 - 주문외출고 등록과 동일 프로세스
     * - uploadType: [com.smartfoodnet.fnproduct.order.vo.OrderUploadType.RE_ORDER]
     *
     *   @see com.smartfoodnet.fnproduct.release.ManualReleaseService.issueManualRelease
     */
    @Transactional
    fun issueReOrder(partnerId: Long, createModel: ReOrderCreateModel): ManualOrderResponseModel {
        ValidatorUtils.validateAndThrow(manualOrderModelValidator, createModel)
        return createManualOrder(partnerId, createModel)
    }

    private fun createManualOrder(
        partnerId: Long,
        request: ManualOrderModel
    ): ManualOrderResponseModel {
        // 1. collected order생성하기
        val collectedOrder = createCollectedOrder(partnerId, request)

        // 2. confirm product생성하기
        val confirmProducts =
            createConfirmProducts(collectedOrder, request.products)
        confirmProducts.forEach { collectedOrder.addConfirmProduct(it) }
        collectedOrder.nextStep()

        // 3. confirm order
        val confirmOrder = confirmOrderService.requestOrder(
            partnerId = partnerId,
            requestOrderCreateModel = RequestOrderCreateModel(
                promotion = request.promotion,
                reShipmentReason = request.reShipmentReason,
                collectedOrderIds = listOf(collectedOrder.id!!),
                deliveryType = request.deliveryType
            )
        )
        return ManualOrderResponseModel.from(collectedOrder, confirmOrder)
    }

    /**
     * 주문외출고를 주문수집 데이터로 생성한다
     */
    private fun createCollectedOrder(
        partnerId: Long,
        manualOrderRequest: ManualOrderModel
    ): CollectedOrder {
        val orderUniqueKey = ManualOrderModel.generateOrderUniqueKey()
        if (collectedOrderRepository.existsByOrderUniqueKey(orderUniqueKey)) {
            throw BaseRuntimeException(errorMessage = "Duplicate OrderKey = $orderUniqueKey")
        }

        val collectedOrder = manualOrderRequest.toCollectOrderEntity(
            partnerId = partnerId,
            orderUniqueKey = orderUniqueKey,
            uploadType = manualOrderRequest.uploadType
        )
        collectedOrderRepository.save(collectedOrder)

        return collectedOrder
    }

    /**
     * 주문외출고 상품들을 confirm product로 변환한다
     */
    private fun createConfirmProducts(
        collectedOrder: CollectedOrder,
        manualReleaseProducts: List<ManualProductModel>
    ): List<ConfirmProduct> {
        val basicProductIds = manualReleaseProducts.map { it.productId }.toSet()
        val basicProductsMap = basicProductRepository
            .findAllByIdIn(basicProductIds)
            .associateBy { it.id }

        val confirmProducts = manualReleaseProducts.map {
            val basicProduct = basicProductsMap[it.productId]
                ?: throw BaseRuntimeException(errorMessage = "기본상품을 찾지 못했습니다. productId = ${it.productId}")

            it.toConfirmProduct(collectedOrder, basicProduct, MatchingType.AUTO)
        }.toList()

        confirmProductRepository.saveAll(confirmProducts)

        return confirmProducts
    }

    companion object : Log
}
