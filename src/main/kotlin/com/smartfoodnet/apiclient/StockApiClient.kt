package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.NosnosStockModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class StockApiClient(
    @Value("\${sfn.service.fn-nosnos}") override val host: String,
    override val restTemplate: RestTemplate
) : RestTemplateClient(host, restTemplate) {
    //    val baseUrl = "fn-warehouse-service.sfn-dev"
    val baseUrl = "http://localhost:4001/fresh-networks/fn-warehouse-service"

    fun getStocks(shippingProductId: Long): List<NosnosStockModel>? {
        return get(baseUrl + "/stock/${shippingProductId}")
    }

    fun getStocks(partnerId: Long, shippingProductIds: List<Long?>?): List<NosnosStockModel>? {
        var uriBuilder = UriComponentsBuilder.fromUriString("${baseUrl}/stock").queryParam("memberId", partnerId)

        if (shippingProductIds != null) {
            uriBuilder = uriBuilder.queryParam("shippingProductIds", shippingProductIds)
        }

        val uri = uriBuilder.build().toString()
        val stocksDataModel = get<CommonDataListModel<NosnosStockModel>>(uri)
        return stocksDataModel?.dataList
    }


}