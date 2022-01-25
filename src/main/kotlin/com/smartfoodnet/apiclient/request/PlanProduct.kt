package com.smartfoodnet.apiclient.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.swagger.annotations.ApiModelProperty

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PlanProduct(
    @ApiModelProperty(value = "출고상품 ID")
    val shippingProductId: Long,
    @ApiModelProperty(value = "예정수량")
    val quantity: Long
)