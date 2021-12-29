package com.smartfoodnet.fninventory.shortage

import com.smartfoodnet.apiclient.StockApiClient
import com.smartfoodnet.apiclient.response.NosnosStockModel
import com.smartfoodnet.fninventory.shortage.model.ProductShortageModel
import com.smartfoodnet.fninventory.shortage.model.ShortageOrderProjectionModel
import com.smartfoodnet.fnproduct.order.OrderService
import com.smartfoodnet.fnproduct.order.model.OrderStatus
import com.smartfoodnet.fnproduct.order.support.OrderDetailRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ShortageService(
    private val orderService: OrderService,
    private val stockApiClient: StockApiClient
) {
    private val API_CALL_LIST_SIZE = 50

    fun getProductShortages_back(partnerId: Long): List<ProductShortageModel> {
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
                        totalShortagePrice = totalShortagePrice,
                        totalOrderCount = totalOrderCount
                    )
                )
            }
        }

        return productShortageModels
    }

    fun getProductShortages(partnerId: Long): List<ProductShortageModel> {

        val shortageProjections = orderService.getShortageProjectionModel(partnerId = partnerId, status = OrderStatus.NEW) ?: return listOf()

        val arrShortageProjections = shortageProjections.chunked(API_CALL_LIST_SIZE)

        val nosnosStocks = mutableListOf<NosnosStockModel>()

        arrShortageProjections.forEach { shortageProjections
            val nosnosStock = stockApiClient.getStocks(
                partnerId = partnerId,
                shippingProductIds = shortageProjections?.map {
                    it.shippingProductId
                }
            ) ?: listOf()
            nosnosStocks.addAll(nosnosStock)
        }

        val productShortageModels = mutableListOf<ProductShortageModel>()

        nosnosStocks?.forEach{
            val shortageProjection = shortageProjections.filter { projection ->
                projection.shippingProductId?.equals(it.shippingProductId) ?: false
            }.firstOrNull() ?: return@forEach

            if((shortageProjection?.totalOrderCount ?: 0) > it.normalStock!!) {
                productShortageModels.add(
                    ProductShortageModel(
                        availableStockCount = it.normalStock,
                        shortageCount = shortageProjection.totalOrderCount?.minus(it.normalStock),
                        shortageOrderCount = shortageProjection.shortageOrderCount?.toInt(),
                        totalShortagePrice = shortageProjection.totalShortagePrice,
                        totalOrderCount = shortageProjection.totalOrderCount
                    )
                )
            }
        }

        return productShortageModels
    }
}