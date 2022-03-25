package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.annotation.JsonProperty

class PostOutboundModel(
    @JsonProperty("order_code")
    val orderCode: String,
    @JsonProperty("order_id")
    val orderId: Long
)
