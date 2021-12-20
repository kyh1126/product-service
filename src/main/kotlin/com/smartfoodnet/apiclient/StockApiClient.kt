package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.response.NosnosStockModel
import org.springframework.stereotype.Component

@Component
class StockApiClient : RestTemplateClient() {
    fun getStocks(shippingProductId: Long): List<NosnosStockModel>? {
        return get("/inventory/stocks/${shippingProductId}")
    }

    fun getStocks(partnerId: Long, shippingProductIds: List<Int?>): List<NosnosStockModel>? {
        var queryString = "partner_id=${partnerId}&shippingProductIds="

        shippingProductIds.forEachIndexed { index, id ->
            queryString += "&${id},"
        }

        queryString = queryString.substring(0, queryString.length - 1)

        return get("/inventory/stocks?${queryString}")
    }
}

