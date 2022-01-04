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
            val orderDetailsByBasicProduct = orderDetails?.filter { orderDetail ->
                orderDetail.storeProduct?.basicProduct?.shippingProductId?.equals(it.shippingProductId) ?: false
            }

            val totalOrderCount = orderDetailsByBasicProduct?.sumOf { it.count ?: 1 }  ?: 0

            val totalShortagePrice = orderDetailsByBasicProduct?.sumOf { it.price ?: 0.0  }

            if(totalOrderCount > it.normalStock!!) {
                productShortageModels.add(
                    ProductShortageModel(
                        availableStockCount = it.normalStock,
                        shortageCount = totalOrderCount - it.normalStock,
                        shortageOrderCount = orderDetailsByBasicProduct?.size ?: 0,
                        totalShortageOrderPrice = totalShortagePrice,
                        totalOrderCount = totalOrderCount
                    )
                )
            }
        }

        return productShortageModels
    }
}