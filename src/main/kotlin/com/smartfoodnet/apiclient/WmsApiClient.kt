package com.smartfoodnet.apiclient

import com.fasterxml.jackson.core.type.TypeReference
import com.smartfoodnet.apiclient.request.PreShippingProductModel
import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.NosnosExpirationDateStockModel
import com.smartfoodnet.apiclient.response.NosnosStockModel
import com.smartfoodnet.apiclient.response.PostShippingProductModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class WmsApiClient(
    @Value("\${sfn.service.fn-warehouse-management-service}") override val host: String,
    override val restTemplate: RestTemplate,
    private val feignClient: WmsFeignClient
) : RestTemplateClient(host, restTemplate) {
    fun getStocksByExpirationDate(partnerId: Long, shippingProductIds: List<Long>): List<NosnosExpirationDateStockModel>? {
        val uriBuilder = UriComponentsBuilder.fromUriString("/stock/expire")
            .queryParam("memberId", partnerId)
            .queryParam("shippingProductIds", shippingProductIds)

        val uri = uriBuilder.build().toString()
        val stocksDataModel = getSimple<CommonDataListModel<NosnosStockModel>>(uri = uri)
        return objectMapper.convertValue(
            stocksDataModel?.dataList,
            object : TypeReference<List<NosnosExpirationDateStockModel>>() {})
    }

    fun getStocks(partnerId: Long, shippingProductIds: List<Long?>?): List<NosnosStockModel>? {
        var uriBuilder =
            UriComponentsBuilder.fromUriString("/stock").queryParam("memberId", partnerId)

        if (shippingProductIds != null) {
            uriBuilder = uriBuilder.queryParam("shippingProductIds", shippingProductIds)
        }

        val uri = uriBuilder.build().toString()
        val stocksDataModel = getSimple<CommonDataListModel<NosnosStockModel>>(uri = uri)
        return objectMapper.convertValue(
            stocksDataModel?.dataList,
            object : TypeReference<List<NosnosStockModel>>() {})
    }

    fun getStocksFeign(partnerId: Long, shippingProductIds: List<Long?>?): List<NosnosStockModel>? {
        var uriBuilder =
            UriComponentsBuilder.fromUriString("/stock").queryParam("memberId", partnerId)

        if (shippingProductIds != null) {
            uriBuilder = uriBuilder.queryParam("shippingProductIds", shippingProductIds)
        }

        val uri = uriBuilder.build().toString()
        val stocksDataModel = feignClient.getStocks(partnerId, shippingProductIds).payload
        return objectMapper.convertValue(
            stocksDataModel?.dataList,
            object : TypeReference<List<NosnosStockModel>>() {})
    }

    fun createShippingProduct(preModel: PreShippingProductModel): PostShippingProductModel? {
        return post("/shipping/products/shipping_product", preModel)
    }

    fun updateShippingProduct(shippingProductId: Long, preModel: PreShippingProductModel) {
        return put("/shipping/products", preModel)
    }
}
