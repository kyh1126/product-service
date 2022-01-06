package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.request.PreShippingProductModel
import com.smartfoodnet.apiclient.response.PostShippingProductModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class WmsApiClient(
    @Value("\${sfn.service.fn-warehouse-management-service}") override val host: String,
    override val restTemplate: RestTemplate
) : RestTemplateClient(host, restTemplate) {
    fun createShippingProduct(preModel: PreShippingProductModel): PostShippingProductModel? {
        return post("/shipping/products", preModel)
    }

    fun updateShippingProduct(shippingProductId: Long, preModel: PreShippingProductModel) {
        return put("/shipping/products/$shippingProductId", preModel)
    }
}
