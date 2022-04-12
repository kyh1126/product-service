package com.smartfoodnet.apiclient.response


import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ReturnCreateItem(
    val releaseReturnItemRenualId: Long? = null,
    val releaseItemId: Long? = null,
    val shippingProductId: Long,
    val quantity: Int
)
