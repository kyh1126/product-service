package com.smartfoodnet.fninventory.shortage

import com.smartfoodnet.apiclient.response.NosnosStockModel
import com.smartfoodnet.common.Constants.API_CALL_LIST_SIZE
import com.smartfoodnet.fninventory.shortage.model.ProductShortageModel
import com.smartfoodnet.fninventory.shortage.support.ProductShortageSearchCondition
import com.smartfoodnet.fnproduct.order.OrderService
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CompletableFuture

@Service
@Transactional(readOnly = true)
class ShortageService(
    private val orderService: OrderService,
    private val shortageAsyncService: ShortageAsyncService
) {
    fun getProductShortages(partnerId: Long, condition: ProductShortageSearchCondition): List<ProductShortageModel> {
        val shortageProjections =
            orderService.getShortageProjectionModel(partnerId = partnerId, status = OrderStatus.NEW, condition)
                ?: return listOf()
        val shippingProductIds = shortageProjections.map { it.shippingProductId }

        val nosnosStocks = getNosnosStocksByChunk(partnerId, shippingProductIds)

        val productShortageModels = mutableListOf<ProductShortageModel>()

        nosnosStocks.forEach {
            val shortageProjection = shortageProjections.firstOrNull { projection ->
                projection.shippingProductId?.equals(it.shippingProductId) ?: false
            } ?: return@forEach

            if ((shortageProjection.totalOrderCount ?: 0) > it.normalStock!!) {
                val productShortageModel = ProductShortageModel(
                    basicProductId = shortageProjection.basicProductId,
                    basicProductName = shortageProjection.basicProductName,
                    basicProductCode = shortageProjection.basicProductCode,
                    availableStockCount = it.normalStock,
                    shortageCount = shortageProjection.totalOrderCount?.minus(it.normalStock),
                    shortageOrderCount = shortageProjection.shortageOrderCount?.toInt(),
                    totalShortagePrice = shortageProjection.totalShortagePrice,
                    totalOrderCount = shortageProjection.totalOrderCount
                )

                productShortageModels.add(productShortageModel)
            }
        }

        return productShortageModels
    }

    private fun getNosnosStocksByChunk(
        partnerId: Long,
        shippingProductIds: List<Long?>
    ): List<NosnosStockModel> {
        val shippingProductIdChunks = shippingProductIds.chunked(API_CALL_LIST_SIZE)
        val futures = mutableListOf<CompletableFuture<List<NosnosStockModel>>>()
        shippingProductIdChunks.forEach { shippingProductIds ->
            val future = shortageAsyncService.getNosnosStocksAsync(partnerId = partnerId, shippingProductIds = shippingProductIds)

            futures.add(future)
        }

        CompletableFuture.allOf(*futures.toTypedArray())

        return futures.flatMap { it.get()}
    }
}
