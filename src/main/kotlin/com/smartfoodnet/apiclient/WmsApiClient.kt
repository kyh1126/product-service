package com.smartfoodnet.apiclient

import com.fasterxml.jackson.core.type.TypeReference
import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.NosnosExpirationDateStockModel
import com.smartfoodnet.apiclient.response.NosnosStockModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class StockApiClient(
    @Value("\${sfn.service.fn-warehouse-management-service}") override val host : String,
    override val restTemplate: RestTemplate
) : RestTemplateClient(host, restTemplate) {
    fun getStocks(shippingProductId: Long): List<NosnosStockModel>? {
        return get(host + "/stock/${shippingProductId}")
    }

    fun getStocksByExpirationDate(shippingProductIds: List<Long>): List<NosnosExpirationDateStockModel>? {
        val uriBuilder = UriComponentsBuilder.fromUriString("/stocks_by_best_before").queryParam("shippingProductIds", shippingProductIds)

        val uri = uriBuilder.build().toString()
        val stocksDataModel = getSimple<CommonDataListModel<NosnosStockModel>>(uri = uri)
        return objectMapper.convertValue(stocksDataModel?.dataList, object : TypeReference<List<NosnosExpirationDateStockModel>>() {})
    }

    fun getStocks(partnerId: Long, shippingProductIds: List<Long?>?): List<NosnosStockModel>? {
        var uriBuilder = UriComponentsBuilder.fromUriString("/stock").queryParam("memberId", partnerId)

        if (shippingProductIds != null) {
            uriBuilder = uriBuilder.queryParam("shippingProductIds", shippingProductIds)
        }

        val uri = uriBuilder.build().toString()
        val stocksDataModel = getSimple<CommonDataListModel<NosnosStockModel>>(uri = uri)
        return objectMapper.convertValue(stocksDataModel?.dataList, object : TypeReference<List<NosnosStockModel>>() {})
    }
}