package com.smartfoodnet.fninventory.shortage

import com.smartfoodnet.apiclient.StockApiClient
import com.smartfoodnet.apiclient.response.NosnosStockModel
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
    private val API_CALL_LIST_SIZE = 50

    fun getProductShortages(partnerId: Long): List<ProductShortageModel> {
        val shortageProjections =
            orderService.getShortageProjectionModel(partnerId = partnerId, status = OrderStatus.NEW) ?: return listOf()
        val shippingProductIds = shortageProjections.map { it.shippingProductId }

        val nosnosStocks = getNosnosStocksByChunk(partnerId, shippingProductIds)

        val productShortageModels = mutableListOf<ProductShortageModel>()

        nosnosStocks.forEach {
            val shortageProjection = shortageProjections.filter { projection ->
                projection.shippingProductId?.equals(it.shippingProductId) ?: false
            }.firstOrNull() ?: return@forEach

            if ((shortageProjection.totalOrderCount ?: 0) > it.normalStock!!) {
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

    private fun getNosnosStocksByChunk(partnerId: Long, shippingProductIds: List<Long?>): List<NosnosStockModel> {
        val nosnosStocks = mutableListOf<NosnosStockModel>()

        val arrShippingProductIds = shippingProductIds.chunked(API_CALL_LIST_SIZE)
        arrShippingProductIds.forEach { idChunks ->
            val stocks = stockApiClient.getStocks(
                partnerId = partnerId,
                shippingProductIds = idChunks
            ) ?: listOf()

            nosnosStocks.addAll(stocks)
        }

        return nosnosStocks
    }

}