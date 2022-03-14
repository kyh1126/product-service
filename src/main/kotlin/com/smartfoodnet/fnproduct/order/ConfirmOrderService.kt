package com.smartfoodnet.fnproduct.order

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fnproduct.order.dto.ConfirmOrderModel
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.order.entity.ConfirmPackageProduct
import com.smartfoodnet.fnproduct.order.entity.ConfirmProduct
import com.smartfoodnet.fnproduct.order.model.BasicProductAddModel
import com.smartfoodnet.fnproduct.order.model.OrderStatus
import com.smartfoodnet.fnproduct.order.support.ConfirmOrderRepository
import com.smartfoodnet.fnproduct.order.support.condition.ConfirmOrderSearchCondition
import com.smartfoodnet.fnproduct.order.vo.MatchingType
import com.smartfoodnet.fnproduct.product.BasicProductService
import com.smartfoodnet.fnproduct.product.PackageProductMappingRepository
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ConfirmOrderService(
    val orderService: OrderService,
    val confirmOrderRepository: ConfirmOrderRepository,
    val basicProductService: BasicProductService,
    val packageProductMappingRepository: PackageProductMappingRepository,
    val wmsApiClient: WmsApiClient
) : Log {
    @Transactional
    fun createConfirmOrder(partnerId: Long, collectedOrderIds: List<Long>) {
        val collectedList = orderService.getCollectedOrders(collectedOrderIds)

        val subtractList = collectedOrderIds.subtract(collectedList.map { it.id }.toSet())
        if (subtractList.isNotEmpty()) {
            throw BaseRuntimeException(errorMessage = "주문수집 Key[${subtractList.joinToString { it.toString() }}]를 찾을 수 없습니다")
        }

        if (!collectedList.all { it.partnerId == partnerId })
            throw BaseRuntimeException(errorMessage = "해당 고객사의 주문건이 아닙니다")

        if (!collectedList.all { it.status == OrderStatus.NEW })
            throw BaseRuntimeException(errorMessage = "이미 출고지시가 처리된 데이터 입니다")

        collectedList.groupBy {
            it.bundleNumber
        }.forEach {
            convertConfirmOrderEntity(partnerId, it.key, it.value)
        }
    }

    private fun convertConfirmOrderEntity(
        partnerId: Long,
        bundleNumber: String,
        collectedList: List<CollectedOrder>
    ) {
        val confirmOrder = ConfirmOrder(
            partnerId = partnerId,
            bundleNumber = bundleNumber
        )

        collectedList.forEach {
            val storeMapping = it.storeProduct?.storeProductMappings
            if (storeMapping.isNullOrEmpty())
                throw BaseRuntimeException(errorMessage = "매칭되지 않은 쇼핑몰 상품이 존재합니다 상품코드[${it.collectedProductInfo.collectedStoreProductName} - ${it.collectedProductInfo.collectedStoreProductOptionName}]")

            storeMapping.forEach { storeProductMapping ->
                val basicProduct = storeProductMapping.basicProduct
                val orderQuantity = (it.quantity ?: 1) * storeProductMapping.quantity
                createConfirmProductAndPackageProduct(it, confirmOrder, basicProduct, orderQuantity)
            }
            it.status = it.status.next()
        }

        confirmOrderRepository.save(confirmOrder)
    }

    private fun createConfirmProductAndPackageProduct(
        collectedOrder: CollectedOrder,
        confirmOrder: ConfirmOrder,
        basicProduct: BasicProduct,
        orderQuantity: Int,
        matchingType: MatchingType = MatchingType.AUTO
    ) {
        val confirmProduct = ConfirmProduct(
            type = basicProduct.type,
            matchingType = matchingType,
            basicProduct = basicProduct,
            confirmOrder = confirmOrder,
            quantity = orderQuantity,
            quantityPerUnit = orderQuantity / collectedOrder.quantity,
            collectedOrder = collectedOrder
        )

        if (basicProduct.type == BasicProductType.PACKAGE) {
            basicProduct.packageProductMappings.forEach {
                val selectedBasicProduct = it.selectedBasicProduct
                confirmProduct.confirmPackageProductList.add(
                    ConfirmPackageProduct(
                        confirmProduct = confirmProduct,
                        basicProduct = selectedBasicProduct,
                        quantity = orderQuantity * it.quantity
                    )
                )
            }
        }

        confirmOrder.addConfirmProduct(confirmProduct)
    }

    private fun getAvailableStocks(
        partnerId: Long,
        shippingProductIds: List<Long>
    ): Map<Long, Int> {
        if (shippingProductIds.isEmpty()) return mapOf()

        return try {
            wmsApiClient
                .getStocks(partnerId, shippingProductIds).payload!!.dataList
                .associateBy({ it.shippingProductId }, { it.normalStock ?: 0 })
        } catch (e: Exception) {
            e.printStackTrace()
            log.info("partnerId:${partnerId}의 재고 정보[${shippingProductIds.joinToString(",")}]를 가져올 수 없습니다")
            mapOf()
        }
    }

    private fun getMissingPackageProductStocks(partnerId: Long, shippingProductIds: List<Long>, availableStocks: MutableMap<Long, Int>){
        if (shippingProductIds.isNotEmpty()) {
            val chunkedList = shippingProductIds.chunked(100)
            chunkedList.forEach {
                availableStocks.putAll(
                    getAvailableStocks(
                        partnerId,
                        it
                    )
                )
            }
        }
    }

    private fun getPackageAvailableMinStocks(
        confirmOrderModel: ConfirmOrderModel,
        availableStocks: MutableMap<Long, Int>
    ): Int {
        val basicProductId = confirmOrderModel.basicProductId!!

        // TODO : 모음상품의 기본상품셋을 가져온다
        val basicProductAndQuantity =
            packageProductMappingRepository.findAllByBasicProductId(basicProductId)
                .associateBy({ it.selectedBasicProduct }, { it.quantity })

        val shippingProductIds =
            basicProductAndQuantity.map {
                it.key.shippingProductId!!
            }.filter {
                !availableStocks.containsKey(it)
            }

        getMissingPackageProductStocks(confirmOrderModel.partnerId, shippingProductIds, availableStocks)

        val mappedQuantity = confirmOrderModel.mappedQuantityCalc
        val minQuantity = basicProductAndQuantity.map {
            var quantity = it.value * mappedQuantity
            val availableStock = availableStocks[it.key.shippingProductId] ?: 0
            availableStock.toDouble().div(quantity.toDouble())
        }.minOf { it.toInt() }

        return minQuantity
    }

    fun getConfirmOrderWithPageable(
        condition: PredicateSearchCondition,
        page: Pageable
    ): PageResponse<ConfirmOrderModel> {
        val response = confirmOrderRepository.findAllByConfirmOrderWithPageable(condition, page)
        return PageResponse.of(response)
    }

    fun getConfirmOrder(condition: ConfirmOrderSearchCondition): List<ConfirmOrderModel> {
        val partnerId = condition.partnerId!!
        val response = confirmOrderRepository.findAllByConfirmOrder(condition)
        val shippingProductIds = response.filter { it.basicProductType == BasicProductType.BASIC }
            .mapNotNull { it.basicProductShippingProductId }
        val availableStocks = getAvailableStocks(partnerId, shippingProductIds).toMutableMap()

        response.forEach {
            it.availableQuantity =
                if (it.basicProductType == BasicProductType.PACKAGE) getPackageAvailableMinStocks(it, availableStocks)
                else availableStocks[it.basicProductShippingProductId]?.toInt() ?: -1
        }

        return response
    }

    @Transactional
    fun addBasicProduct(confirmOrderId : Long, basicProductAddModel: BasicProductAddModel){
        val collectedOrder = orderService.getCollectedOrder(basicProductAddModel.collectedOrderId)

        val confirmOrder = confirmOrderRepository.findById(confirmOrderId).get()
        confirmOrder.confirmProductList.clear()

        val basicProducts =
            basicProductService.getBasicProducts(basicProductAddModel.basicProducts.map { it.basicProductId })

        val quantityPerBasicProduct = getBasicProductsAndRequestQuantityMapping(basicProductAddModel)

        basicProducts.forEach {
            createConfirmProductAndPackageProduct(collectedOrder, confirmOrder, it, quantityPerBasicProduct[it.id]!!, MatchingType.TEMP)
        }

    }

    private fun getBasicProductsAndRequestQuantityMapping(basicProductAddModel: BasicProductAddModel) : Map<Long, Int>{
        return basicProductAddModel.basicProducts.associateBy({it.basicProductId},{it.quantity})
    }
}