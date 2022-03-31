package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.annotation.JsonProperty

data class NosnosSalesProductModel(
    @JsonProperty("sales_product_id")
    val salesProductId: Long,
    @JsonProperty("sales_product_code")
    val salesProductCode: String
)
