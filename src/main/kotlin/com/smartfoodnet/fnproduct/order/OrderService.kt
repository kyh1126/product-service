package com.smartfoodnet.fnproduct.order

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fnproduct.order.model.CollectedOrderCreateModel
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fninventory.shortage.model.ShortageOrderProjectionModel
import com.smartfoodnet.fninventory.shortage.support.ProductShortageSearchCondition
import com.smartfoodnet.fnproduct.order.dto.CollectedOrderModel
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.model.BasicProductAddModel
import com.smartfoodnet.fnproduct.order.support.CollectedOrderRepository
import com.smartfoodnet.fnproduct.order.support.condition.CollectingOrderSearchCondition
import com.smartfoodnet.fnproduct.product.BasicProductService
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.store.StoreProductService
import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import com.smartfoodnet.fnproduct.store.entity.StoreProductMapping
import com.smartfoodnet.fnproduct.store.support.StoreProductSearchCondition
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderService(
    val collectedOrderRepository: CollectedOrderRepository,
    val storeProductService: StoreProductService,
    val basicProductService: BasicProductService,
    val wmsApiClient: WmsApiClient
) {
    private fun convertCollectedOrderEntity(collectedOrderCreateModel: CollectedOrderCreateModel) {
        if (collectedOrderRepository.existsByOrderUniqueKey(collectedOrderCreateModel.orderUniqueKey)) {
            log.info("Duplicate OrderKey = ${collectedOrderCreateModel.orderUniqueKey}")
            return
        }

        val collectedOrder = collectedOrderCreateModel.toCollectEntity()

        val storeProduct = storeProductService
            .getStoreProductForOrderDetail(
                StoreProductSearchCondition.toSearchConditionModel(collectedOrder)
            )

        collectedOrder.storeProduct = storeProduct

        collectedOrderRepository.save(collectedOrder)
    }

    @Transactional
    fun createCollectedOrder(collectedOrderCreateModel: List<CollectedOrderCreateModel>) {
        collectedOrderCreateModel.map { convertCollectedOrderEntity(it) }
    }

    fun getCollectedOrder(
        condition: CollectingOrderSearchCondition
    ): List<CollectedOrderModel> {
        return collectedOrderRepository.findAllCollectedOrders(condition)
    }

    fun getCollectedOrder(collectedOrderId: Long) =
        collectedOrderRepository.findById(collectedOrderId).get()

    fun getCollectedOrders(partnerId: Long, status: OrderStatus): List<CollectedOrder>? {
        return collectedOrderRepository.findAllByPartnerIdAndStatus(partnerId, status)
    }

    fun getCollectedOrders(collectedOrderIds: List<Long>): List<CollectedOrder> {
        return collectedOrderRepository.findAllById(collectedOrderIds)
    }

    fun getShortageProjectionModel(
        partnerId: Long,
        status: OrderStatus,
        condition: ProductShortageSearchCondition
    ): List<ShortageOrderProjectionModel>? {
        return collectedOrderRepository.findAllByPartnerIdAndStatusGroupByProductId(
            partnerId,
            status,
            condition
        )
    }

    fun getOrderCountByProductIdAndStatus(productId: Long, status: OrderStatus): Int? {
        return collectedOrderRepository.getCountByProductIdAndStatusGroupByProductId(
            productId,
            status
        )
    }

    @Transactional
    fun addStoreProduct(basicProductAddModel: BasicProductAddModel) {
        // TODO : CollectedOrder에 이미 쇼핑몰 상품이 매칭되어있다면 오류
        val collectedOrder = collectedOrderRepository.findById(basicProductAddModel.collectedOrderId).get()

        if (collectedOrder.partnerId != basicProductAddModel.partnerId) {
            throw BaseRuntimeException(errorMessage = "해당 고객사의 주문이 아닙니다")
        }

        if (collectedOrder.isConnectedStoreProduct) {
            throw BaseRuntimeException(errorMessage = "이미 매칭된 쇼핑몰 상품이 있습니다")
        }

        // 1. 주문수집시 쇼핑몰상품 매칭기준에 따라 조회 후 없으면 쇼핑몰 생성
        val storeProduct =
            storeProductService.getStoreProductForOrderDetail(
                StoreProductSearchCondition.toSearchConditionModel(collectedOrder)
            ) ?: createStoreProduct(collectedOrder)

        if (storeProduct.storeProductMappings.isEmpty()) {
            // 3. 쇼핑몰 상품 정보에 기본/모음상품을 같이 매핑등록
            val basicProducts = basicProductAddModel.basicProducts
            val findBasicProducts =
                basicProductService.getBasicProducts(basicProducts.map { it.basicProductId })
                    .associateBy { it.id!! }

            val mappedBasicProducts = mutableMapOf<BasicProduct, Int>()
            basicProducts.forEach {
                val basicProduct = findBasicProducts[it.basicProductId]
                if (basicProduct != null) {
                    mappedBasicProducts[basicProduct] = it.quantity
                }
            }

            // 4. 주문수집에 해당 쇼핑몰 상품을 매칭
            mappedBasicProducts.forEach {
                val storeProductMapping = StoreProductMapping(
                    storeProduct = storeProduct,
                    basicProduct = it.key,
                    quantity = it.value
                )
                storeProduct.storeProductMappings.add(storeProductMapping)
            }
        }

        // TODO : 생성된 쇼핑몰 상품을 연결 한다
        collectedOrder.storeProduct = storeProduct
    }

    private fun createStoreProduct(collectedOrder: CollectedOrder): StoreProduct {
        val storeProduct = with(collectedOrder) {
            StoreProduct(
                storeId = storeId ?: throw IllegalArgumentException("storeId가 없습니다"),
                partnerId = partnerId,
                storeName = storeName,
                name = collectedProductInfo.collectedStoreProductName
                    ?: throw IllegalArgumentException("상품명이 없습니다"),
                storeProductCode = collectedProductInfo.collectedStoreProductCode,
                optionName = collectedProductInfo.collectedStoreProductOptionName,
                optionCode = collectedProductInfo.collectedStoreProductOptionCode
            )
        }

        return storeProductService.createStoreProduct(storeProduct)
    }

    companion object : Log
}