package com.smartfoodnet.fnproduct.order

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.request.RequestOrderMapper
import com.smartfoodnet.common.Constants.API_CALL_LIST_SIZE
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.error.exception.ErrorCode
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fninventory.stock.dto.BasicProductAvailableStock
import com.smartfoodnet.fninventory.stock.model.AvailableStockModel
import com.smartfoodnet.fnproduct.order.dto.ConfirmProductModel
import com.smartfoodnet.fnproduct.order.entity.*
import com.smartfoodnet.fnproduct.order.model.ConfirmProductAddModel
import com.smartfoodnet.fnproduct.order.model.ConfirmProductMappedModel
import com.smartfoodnet.fnproduct.order.model.RequestOrderCreateModel
import com.smartfoodnet.fnproduct.order.support.ConfirmOrderRepository
import com.smartfoodnet.fnproduct.order.support.ConfirmProductRepository
import com.smartfoodnet.fnproduct.order.support.condition.ConfirmProductSearchCondition
import com.smartfoodnet.fnproduct.order.vo.DeliveryType
import com.smartfoodnet.fnproduct.order.vo.MatchingType
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.product.BasicProductService
import com.smartfoodnet.fnproduct.product.PackageProductMappingRepository
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.release.ReleaseInfoStoreService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class ConfirmOrderService(
    val orderService: OrderService,
    val basicProductService: BasicProductService,
    val releaseInfoStoreService: ReleaseInfoStoreService,
    val boxService: BoxService,

    val confirmOrderRepository: ConfirmOrderRepository,
    val confirmProductRepository: ConfirmProductRepository,
    val packageProductMappingRepository: PackageProductMappingRepository,

    val wmsApiClient: WmsApiClient
) : Log {
    @Transactional
    fun createConfirmProduct(partnerId: Long, collectedOrderIds: List<Long>) {
        val collectedList = validateConfirmProduct(partnerId, collectedOrderIds)

        if (!collectedList.all { it.status == OrderStatus.NEW })
            throw BaseRuntimeException(errorMessage = "이미 출고지시가 처리된 데이터 입니다")

        collectedList.forEach {
            convertConfirmProduct(it)
        }
    }

    private fun convertConfirmProduct(collectedOrder: CollectedOrder) {
        collectedOrderAddConfirmProduct(collectedOrder)
        collectedOrder.nextStep()
    }

    @Transactional
    fun restoreConfirmProduct(partnerId: Long, collectedOrderIds: List<Long>) {
        val collectedList = validateConfirmProduct(partnerId, collectedOrderIds)

        if (!collectedList.all { it.status == OrderStatus.ORDER_CONFIRM })
            throw BaseRuntimeException(errorMessage = "출고지시 상태의 주문건이 아닙니다")

        collectedList.forEach {
            restoreConfirmProduct(it)
        }
    }

    @Transactional
    fun requestOrders(
        partnerId: Long,
        requestOrderCreateModel: RequestOrderCreateModel
    ) : List<ConfirmOrder> {
        validateStockPerAvailableStock(partnerId, requestOrderCreateModel)
        return sendNosnosOrders(partnerId, requestOrderCreateModel)
    }

    private fun validateStockPerAvailableStock(
        partnerId: Long,
        requestOrderCreateModel: RequestOrderCreateModel
    ) {
        val collectedOrderList =
            orderService.getCollectedOrders(requestOrderCreateModel.collectedOrderIds)
        val basicProductList : List<BasicProduct> =
            basicProductService.getAllProductFromCollectedOrders(collectedOrderList)

        val shippingProductIds = basicProductList.mapNotNull { it.shippingProductId }.distinct()

        val availableStocks : MutableMap<Long, Int> = mutableMapOf()
        shippingProductIds.chunked(API_CALL_LIST_SIZE).forEach { ids : List<Long> ->
            availableStocks += getAvailableStocks(partnerId, ids)
        }

        val calculatedBasicProduct  = collectedOrderList
            .flatMap { it.confirmProductList }
            .flatMap {
                val basicProductAvailableStock = BasicProductAvailableStock(it.basicProduct)
                basicProductAvailableStock.calcQuantityPerBasicProduct(it.quantity)
            }
            .groupBy({it.basicProduct.shippingProductId!!},{it.quantity})
            .mapValues { it.value.sum() }


        log.info("calculatedBasicProduct -> {}", calculatedBasicProduct)
        log.info("availableStocks -> {}", availableStocks)
        
        val lackShippingProductIds= calculatedBasicProduct
            .filter { it.value.compareTo(availableStocks[it.key] ?: 0) == 1 }
            .map { it.key }

        if (lackShippingProductIds.isNotEmpty())
            throw IllegalStateException("가용재고가 부족한 출고건이 존재합니다 : shippingProduct = $lackShippingProductIds")

    }

    @Transactional
    fun requestOrder(
        partnerId: Long,
        requestOrderCreateModel: RequestOrderCreateModel
    ): ConfirmOrder {
        return sendNosnosOrders(partnerId, requestOrderCreateModel).first()
    }

    private fun sendNosnosOrders(
        partnerId: Long,
        requestOrderCreateModel: RequestOrderCreateModel
    ): List<ConfirmOrder> {
        val collectedOrderList =
            orderService.getCollectedOrders(requestOrderCreateModel.collectedOrderIds)

        if (collectedOrderList.all { it.status != OrderStatus.ORDER_CONFIRM }) {
            throw BaseRuntimeException(errorMessage = "출고지시 상태가 아닌 주문건이 있습니다")
        }

        val sendOrderList = collectedOrderList
            .groupBy { it.bundleNumber }
            .map {
                createConfirmOrder(partnerId, requestOrderCreateModel, it)
            }

        val requestBulkModel = RequestOrderMapper.toOutboundCreateBulkModel(sendOrderList)

        val response =
            wmsApiClient.createOutbounds(requestBulkModel).payload?.processedDataList
                ?: emptyList()

        for ((index, confirmOrder) in sendOrderList.withIndex()) {
            val orderInfo = response[index]
            confirmOrder.setOrderInfo(orderInfo.orderId, orderInfo.orderCode)
            releaseInfoStoreService.createFromOrderInfo(partnerId, orderInfo, confirmOrder)
        }

        return sendOrderList
    }

    fun getConfirmProduct(condition: ConfirmProductSearchCondition): List<ConfirmProductModel> {
        val partnerId = condition.partnerId!!
        val response = confirmProductRepository.findAllCollectedOrderWithConfirmProduct(condition)
        val shippingProductIds = response.filter { it.basicProductType == BasicProductType.BASIC }
            .mapNotNull { it.basicProductShippingProductId }
        val availableStocks = getAvailableStocks(partnerId, shippingProductIds).toMutableMap()

        response.forEach {
            it.availableQuantity =
                if (it.basicProductType == BasicProductType.PACKAGE) getPackageAvailableMinStocks(
                    it,
                    availableStocks
                )
                else availableStocks[it.basicProductShippingProductId]?.toInt() ?: -1
        }

        return response
    }

    @Transactional
    fun replaceConfirmProducts(confirmProductAddModel: ConfirmProductAddModel) {
        confirmProductAddModel.confirmProductList.forEach {
            replaceConfirmProduct(it)
        }
    }

    fun getConfirmOrderByOrderId(orderId: Long): ConfirmOrder {
        return confirmOrderRepository.findByOrderId(orderId)
            ?: throw BaseRuntimeException(errorCode = ErrorCode.NO_ELEMENT)
    }

    private fun validateConfirmProduct(
        partnerId: Long,
        collectedOrderIds: List<Long>,
    ): List<CollectedOrder> {
        val collectedList = orderService.getCollectedOrders(collectedOrderIds)
        val subtractList = collectedOrderIds.subtract(collectedList.map { it.id }.toSet())

        if (subtractList.isNotEmpty()) {
            throw BaseRuntimeException(errorMessage = "주문수집 Key[${subtractList.joinToString { it.toString() }}]를 찾을 수 없습니다")
        }

        if (!collectedList.all { it.partnerId == partnerId })
            throw BaseRuntimeException(errorMessage = "해당 고객사의 주문건이 아닙니다")

        return collectedList
    }

    private fun restoreConfirmProduct(collectedOrder: CollectedOrder) {
        collectedOrder.clearConfirmProduct()
        collectedOrderAddConfirmProduct(collectedOrder)
    }

    private fun collectedOrderAddConfirmProduct(collectedOrder: CollectedOrder) {
        val storeMapping = collectedOrder.storeProduct?.storeProductMappings
        if (storeMapping.isNullOrEmpty())
            throw BaseRuntimeException(errorMessage = "매칭되지 않은 쇼핑몰 상품이 존재합니다 상품코드[${collectedOrder.collectedProductInfo.collectedStoreProductName} - ${collectedOrder.collectedProductInfo.collectedStoreProductOptionName}]")

        storeMapping.forEach {
            collectedOrder.addConfirmProduct(
                ConfirmProduct(
                    type = it.basicProduct.type,
                    matchingType = MatchingType.AUTO,
                    basicProduct = it.basicProduct,
                    quantity = collectedOrder.quantity * it.quantity,
                    quantityPerUnit = it.quantity
                )
            )
        }
    }

    private fun createConfirmOrder(
        partnerId: Long,
        requestOrderCreateModel: RequestOrderCreateModel,
        orderGroup: Map.Entry<String, List<CollectedOrder>>
    ): ConfirmOrder {
        val firstCollectedOrder = orderGroup.value.first()
        val shippingMethodType =
            requestOrderCreateModel.deliveryType?.shippingMethodType
                ?: DeliveryType.PARCEL.shippingMethodType

        val confirmOrder = ConfirmOrder(
            partnerId = partnerId,
            bundleNumber = orderGroup.key,
            shippingMethodType = shippingMethodType.value,
            requestShippingDate = LocalDateTime.now(),
            receiver = firstCollectedOrder.receiver,
            memo = Memo(
                memo1 = firstCollectedOrder.storeName,
                memo2 = requestOrderCreateModel.promotion,
                memo4 = firstCollectedOrder.shippingPrice?.toInt().toString(),
                memo5 = requestOrderCreateModel.reShipmentReason,
            )
        )

        val collectedOrders : List<CollectedOrder> = orderGroup.value
        val basicProductList : List<BasicProduct> = basicProductService.getAllProductFromCollectedOrders(collectedOrders)
        confirmOrder.memo.memo3 = boxService.getRecommendBox(basicProductList).description

        orderGroup.value.forEach {
            val confirmRequestOrder = ConfirmRequestOrder(
                confirmOrder = confirmOrder,
                collectedOrder = it
            )
            confirmOrder.addRequestOrder(confirmRequestOrder)
            it.nextStep()
        }

        return confirmOrderRepository.save(confirmOrder)
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

    private fun getMissingPackageProductStocks(
        partnerId: Long,
        shippingProductIds: List<Long>,
        availableStocks: MutableMap<Long, Int>
    ) {
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
        confirmProductModel: ConfirmProductModel,
        availableStocks: MutableMap<Long, Int>
    ): Int {
        val basicProductId = confirmProductModel.basicProductId!!

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

        getMissingPackageProductStocks(
            confirmProductModel.partnerId,
            shippingProductIds,
            availableStocks
        )

        val mappedQuantity = confirmProductModel.releaseQuantity
        val minQuantity = basicProductAndQuantity.map {
            val quantity = it.value * mappedQuantity
            val availableStock = availableStocks[it.key.shippingProductId] ?: 0
            availableStock.toDouble().div(quantity.toDouble())
        }.minOf { it.toInt() }

        return minQuantity
    }

    private fun replaceConfirmProduct(mappedModel: ConfirmProductMappedModel) {
        val collectedOrder = orderService.getCollectedOrder(mappedModel.collectedOrderId)
        collectedOrder.clearConfirmProduct()

        val basicProducts =
            basicProductService.getBasicProducts(mappedModel.basicProducts.map { it.basicProductId })

        val quantityPerBasicProduct = getBasicProductsAndRequestQuantityMapping(mappedModel)

        basicProducts.forEach {
            collectedOrder.addConfirmProduct(
                it.run {
                    val quantity = quantityPerBasicProduct[this.id]!!
                    ConfirmProduct(
                        type = type,
                        basicProduct = this,
                        matchingType = MatchingType.TEMP,
                        quantity = quantity,
                        quantityPerUnit = 1
                    )
                }
            )
        }

    }

    private fun getBasicProductsAndRequestQuantityMapping(mappedModel: ConfirmProductMappedModel): Map<Long, Int> {
        return mappedModel.basicProducts.associateBy({ it.basicProductId }, { it.quantity })
    }

}
