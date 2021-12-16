package com.smartfoodnet.nosnos.api.inventory.model.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.swagger.annotations.ApiModelProperty

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PlanProduct(
    @ApiModelProperty(value = "출고상품 ID")
    val shippingProductId: Long,
    @ApiModelProperty(value = "예정수량")
    val quantity: Long,
    @ApiModelProperty(value = "입고상태")
    val planProductStatus: Long? = null,
    @ApiModelProperty(value = "유통기한", example = "20211212")
    val expireDate: String? = null,
    @ApiModelProperty(value = "제조일자", example = "20211112")
    val makeDate: String? = null
)