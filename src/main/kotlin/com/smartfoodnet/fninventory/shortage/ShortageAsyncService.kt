package com.smartfoodnet.fninventory.shortage

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.response.NosnosStockModel
import com.smartfoodnet.common.utils.Log
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
class ShortageAsyncService(
    private val wmsApiClient: WmsApiClient
) {
    @Async("shortageTaskExecutor")
    fun getNosnosStocksAsync(partnerId: Long, shippingProductIds: List<Long?>): CompletableFuture<List<NosnosStockModel>> {
        val stocks = wmsApiClient.getStocks(
            partnerId = partnerId,
            shippingProductIds = shippingProductIds
        ).payload?.dataList ?: listOf()

        log.info("stocks: ${stocks.size}")

        return CompletableFuture.completedFuture(stocks)
    }

    companion object: Log
}