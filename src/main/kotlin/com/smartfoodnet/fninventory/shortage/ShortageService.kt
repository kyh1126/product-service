package com.smartfoodnet.fninventory.shortage

import com.smartfoodnet.apiclient.StockApiClient
import com.smartfoodnet.fninventory.shortage.model.ProductShortageModel
import com.smartfoodnet.fnproduct.order.OrderService
import com.smartfoodnet.fnproduct.order.model.OrderStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ShortageService(
    private val orderService: OrderService,
    private val stockApiClient: StockApiClient
) {
    fun getProductShortages(partnerId: Long): List<ProductShortageModel> {
        val orderDetails = orderService.getOrderDetails(partnerId = partnerId, status = OrderStatus.NEW)
        val shippingProductIds = orderDetails?.map {
            it.storeProduct?.basicProduct?.shippingProductId
        }?.distinct()

        val nosnosStocks = stockApiClient.getStocks(
            partnerId = partnerId,
            shippingProductIds = shippingProductIds
        )

        val productShortageModels = mutableListOf<ProductShortageModel>()

        nosnosStocks?.forEach{
            val orderCount = orderDetails?.filter { orderDetail ->
                orderDetail.storeProduct?.basicProduct?.shippingProductId?.equals(it.shippingProductId) ?: false
            }?.size ?: 0

            if(orderCount > it.normalStock!!) {
                productShortageModels.add(
                    ProductShortageModel(
                        totalOrderCount = orderCount,
                        availableStockCount = it.normalStock,
                        shortageCount = orderCount - it.normalStock
                    )
                )
            }
        }

        return productShortageModels
    }
}