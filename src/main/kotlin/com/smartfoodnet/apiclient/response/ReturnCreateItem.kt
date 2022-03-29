package com.smartfoodnet.apiclient.response


import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ReturnCreateItem(
    val releaseReturnItemRenualId: Long,
    val releaseItemId: Long,
    val shippingProductId: Long,
    val quantity: Long
)
