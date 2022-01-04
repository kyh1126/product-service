package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.annotation.JsonProperty

data class PostShippingProductModel(
    @JsonProperty("shipping_product_id")
    val shippingProductId: Long,
    @JsonProperty("product_code")
    val productCode: String,
    @JsonProperty("sales_product_id")
    val salesProductId: Long?,
    @JsonProperty("sales_product_code")
    val salesProductCode: String?,
)
