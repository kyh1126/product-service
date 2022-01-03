package com.smartfoodnet.apiclient

import com.fasterxml.jackson.core.type.TypeReference
import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.NosnosStockModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class StockApiClient(
    @Value("\${sfn.service.fn-warehouse-management-service}") val baseUrl: String,
    override val restTemplate: RestTemplate
) : RestTemplateClient(baseUrl, restTemplate) {
    fun getStocks(shippingProductId: Long): List<NosnosStockModel>? {
        return get(baseUrl + "/stock/${shippingProductId}")
    }

    fun getStocksByBestBefore(shippingProductIds: List<Long>): List<NosnosStockModel>? {
        var uriBuilder = UriComponentsBuilder.fromUriString("${baseUrl}/stocks_by_best_before").queryParam("shippingProductIds", shippingProductIds)

        val uri = uriBuilder.build().toString()
        val stocksDataModel = getSimple<CommonDataListModel<NosnosStockModel>>(uri = uri)
        return objectMapper.convertValue(stocksDataModel?.dataList, object: TypeReference<List<NosnosStockModel>>(){})
        return get(baseUrl + "/stock/${shippingProductIds}")
    }

    fun getStocks(partnerId: Long, shippingProductIds: List<Long?>?): List<NosnosStockModel>? {
        var uriBuilder = UriComponentsBuilder.fromUriString("${baseUrl}/stock").queryParam("memberId", partnerId)

        if (shippingProductIds != null) {
            uriBuilder = uriBuilder.queryParam("shippingProductIds", shippingProductIds)
        }

        val uri = uriBuilder.build().toString()
        val stocksDataModel = getSimple<CommonDataListModel<NosnosStockModel>>(uri = uri)
        return objectMapper.convertValue(stocksDataModel?.dataList, object: TypeReference<List<NosnosStockModel>>(){})
    }


}