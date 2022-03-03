package com.smartfoodnet.fnproduct.order

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.response.NosnosStockModel
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fnproduct.order.model.CollectedOrderCreateModel
import com.smartfoodnet.fnproduct.order.model.OrderStatus
import com.smartfoodnet.fninventory.shortage.model.ShortageOrderProjectionModel
import com.smartfoodnet.fnproduct.order.dto.CollectedOrderModel
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.support.CollectedOrderRepository
import com.smartfoodnet.fnproduct.order.support.condition.CollectingOrderSearchCondition
import com.smartfoodnet.fnproduct.store.StoreProductService
import com.smartfoodnet.fnproduct.store.support.StoreProductSearchCondition
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderService(
    val collectedOrderRepository: CollectedOrderRepository,
    val storeProductService: StoreProductService,
    val wmsApiClient: WmsApiClient
) : Log {

    @Transactional
    fun createCollectedOrder(collectedOrderCreateModel: List<CollectedOrderCreateModel>) {
        collectedOrderCreateModel.map { convert(it) }
    }

    private fun getAvailableStocks(condition: CollectingOrderSearchCondition, shippingProductIds : List<Long>) : Map<Long, NosnosStockModel>{
        if (shippingProductIds.isEmpty()) return mapOf()

        return try {
            wmsApiClient
                .getStocks(condition.partnerId!!, shippingProductIds).payload!!.dataList
                .associateBy { it.shippingProductId!! }
        } catch (e: Exception) {
            e.printStackTrace()
            log.info("partnerId:${condition.partnerId}의 재고 정보[${shippingProductIds.joinToString(",")}]를 가져올 수 없습니다")
            mapOf()
        }
    }

    fun getCollectedOrder(
        condition: CollectingOrderSearchCondition
    ): List<CollectedOrderModel>{
        val response = collectedOrderRepository.findCollectedOrders(condition)
        val shippingProductIds = response.mapNotNull { it.basicProductShippingProductId }
        val availableStocks = getAvailableStocks(condition, shippingProductIds)

        if (availableStocks.isNotEmpty()) {
            response.forEach {
                if (it.basicProductShippingProductId != null) {
                    it.availableQuantity =
                        availableStocks[it.basicProductShippingProductId]?.normalStock ?: 0
                }
            }
        }

        return response
    }

    fun getCollectedOrderWithPage(
        condition: CollectingOrderSearchCondition,
        page: Pageable
    ): PageResponse<CollectedOrderModel> {
        val response = collectedOrderRepository.findCollectedOrdersWithPageable(condition, page)
        val shippingProductIds = response.content.mapNotNull { it.basicProductShippingProductId }
        val availableStocks = getAvailableStocks(condition, shippingProductIds)

        if (availableStocks.isNotEmpty()) {
            response.forEach {
                if (it.basicProductShippingProductId != null) {
                    it.availableQuantity =
                        availableStocks[it.basicProductShippingProductId]?.normalStock ?: 0
                }
            }
        }

        return PageResponse.of(response)
    }


    fun getCollectedOrders(partnerId: Long, status: OrderStatus): List<CollectedOrder>? {
        return collectedOrderRepository.findAllByPartnerIdAndStatus(partnerId, status)
    }

    fun getShortageProjectionModel(
        partnerId: Long,
        status: OrderStatus
    ): List<ShortageOrderProjectionModel>? {
        return collectedOrderRepository.findAllByPartnerIdAndStatusGroupByProductId(
            partnerId,
            status
        )
    }

    fun getOrderCountByProductIdAndStatus(productId: Long, status: OrderStatus): Int? {
        return collectedOrderRepository.getCountByProductIdAndStatusGroupByProductId(
            productId,
            status
        )
    }

    private fun convert(collectedOrderCreateModel: CollectedOrderCreateModel): CollectedOrder {
        val collectedOrder = collectedOrderCreateModel.toCollectEntity()

        val storeProduct = storeProductService
            .getStoreProductForOrderDetail(
                with(collectedOrderCreateModel) {
                    StoreProductSearchCondition(
                        partnerId = partnerId,
                        storeProductName = storeProductName,
                        storeProductOptionName = storeProductOptionName
                    )
                }
            )

        collectedOrder.storeProduct = storeProduct

        collectedOrderRepository.save(collectedOrder)
        return collectedOrder
    }

}