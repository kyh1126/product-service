package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.NosnosStockModel
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class StockApiClient : RestTemplateClient() {
//    val baseUrl = "fn-warehouse-service.sfn-dev"
    val baseUrl = "http://localhost:4001/fresh-networks/fn-warehouse-service"

    fun getStocks(shippingProductId: Long): List<NosnosStockModel>? {
        return get(baseUrl + "/stock/${shippingProductId}")
    }

    fun getStocks(partnerId: Long, shippingProductIds: List<Int?>): List<NosnosStockModel>? {
        val uri = UriComponentsBuilder.fromUriString("${baseUrl}/stock")
            .queryParam("memberId", partnerId)
            .queryParam("shippingProductIds", shippingProductIds)
            .build().toString()
        val stocksDataModel = get< CommonDataListModel<NosnosStockModel>>(uri)
        return stocksDataModel?.dataList
    }
}