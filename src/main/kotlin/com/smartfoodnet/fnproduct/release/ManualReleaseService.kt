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
import com.smartfoodnet.fnproduct.order.model.response.ManualReleaseResponseModel
import com.smartfoodnet.fnproduct.order.support.CollectedOrderRepository
import com.smartfoodnet.fnproduct.order.support.ConfirmProductRepository
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.release.model.ManualReleaseCreateModel
import com.smartfoodnet.fnproduct.release.model.ManualReleaseProductInfo
import com.smartfoodnet.fnproduct.release.validator.ManualReleaseCreateModelValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 주문외출고를 위한 서비스
 */
@Service
@Transactional(readOnly = true)
class ManualReleaseService(
    private val manualReleaseCreateModelValidator: ManualReleaseCreateModelValidator,
    private val collectedOrderRepository: CollectedOrderRepository,
    private val basicProductRepository: BasicProductRepository,
    private val confirmProductRepository: ConfirmProductRepository,
    private val confirmOrderService: ConfirmOrderService,
    private val partnerService: PartnerService,
) {
    /**
     * 주문외출고 등록(상품과 주문 정보 없이 주문수집 데이터를 생성한다)
     * - Collected Order생성(쇼핑몰 주문하고 쇼핑몰 상품 없이 등록
     *   uploadType: [com.smartfoodnet.fnproduct.order.enums.OrderUploadType.MANUAL])
     *
     * - Collected Order생성 > Confirm Product생성 > Confirm Order생성(노스노스 발주 등록/SFN출고지시)
     */
    @Transactional
    fun issueManualRelease(
        sfnMetaUser: SfnMetaUser?,
        partnerId: Long,
        manualReleaseRequest: ManualReleaseCreateModel
    ): ManualReleaseResponseModel {
        println("issueManualRelease 1")
        // validation
        ValidatorUtils.validateAndThrow(
            SaveState.CREATE,
            manualReleaseCreateModelValidator,
            manualReleaseRequest
        )

        println("issueManualRelease 2")
        // partnerService.checkUserPartnerMembership(sfnMetaUser, partnerId)

        println("issueManualRelease 3")
        // 1. collected order생성하기
        val collectedOrder = createCollectedOrder(
            partnerId = partnerId,
            manualReleaseRequest = manualReleaseRequest
        )

        println("issueManualRelease 4")
        // 2. confirm product생성하기
        val confirmProducts =
            createConfirmProducts(collectedOrder, manualReleaseRequest.products)
        confirmProducts.forEach { collectedOrder.addConfirmProduct(it) }
        collectedOrder.nextStep()

        println("issueManualRelease 5")
        // 3. confirm order
        val confirmOrders = confirmOrderService.requestOrders(
            partnerId = partnerId,
            requestOrderCreateModel = RequestOrderCreateModel(
                promotion = manualReleaseRequest.promotion,
                reShipmentReason = manualReleaseRequest.reShipmentReason,
                collectedOrderIds = listOf(collectedOrder.id!!),
                deliveryType = manualReleaseRequest.deliveryType
            )
        )

        println("issueManualRelease 6")
        return ManualReleaseResponseModel.from(collectedOrder, confirmOrders)
    }

    /**
     * 주문외출고를 주문수집 데이터로 생성한다
     */
    private fun createCollectedOrder(
        partnerId: Long,
        manualReleaseRequest: ManualReleaseCreateModel
    ): CollectedOrder {
        val orderUniqueKey = ManualReleaseCreateModel.generateOrderUniqueKey()
        if (collectedOrderRepository.existsByOrderUniqueKey(orderUniqueKey)) {
            throw BaseRuntimeException(errorMessage = "Duplicate OrderKey = $orderUniqueKey")
        }

        val collectedOrder = manualReleaseRequest.toCollectOrderEntity(
            partnerId = partnerId,
            orderUniqueKey = orderUniqueKey
        )
        collectedOrderRepository.save(collectedOrder)

        return collectedOrder
    }

    /**
     * 주문외출고 상품들을 confirm product로 변환한다
     */
    private fun createConfirmProducts(
        collectedOrder: CollectedOrder,
        manualReleaseProducts: List<ManualReleaseProductInfo>
    ): List<ConfirmProduct> {
        val basicProductIds = manualReleaseProducts.map { it.productId }.toSet()
        val basicProductsMap = basicProductRepository
            .findAllByIdIn(basicProductIds)
            .associateBy { it.id }

        val confirmProducts = manualReleaseProducts.map {
            val basicProduct = basicProductsMap[it.productId]
                ?: throw BaseRuntimeException(errorMessage = "기본상품을 찾지 못했습니다. productId = ${it.productId}")
            it.toConfirmProduct(collectedOrder, basicProduct)
        }.toList()

        confirmProductRepository.saveAll(confirmProducts)

        return confirmProducts
    }

    companion object : Log
}
