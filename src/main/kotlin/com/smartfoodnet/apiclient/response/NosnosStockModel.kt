package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
@ApiModel(value = "노스노스 재고 모델")
data class NosnosStockModel(
    @ApiModelProperty(value = "출고상품 ID", example = "454")
    val shippingProductId: Long,
    @ApiModelProperty(value = "입고 재고", example = "77")
    val receivingStock: Int? = null,
    @ApiModelProperty(value = "출고가능 재고", example = "454")
    val normalStock: Int? = null,
    @ApiModelProperty(value = "출고지시 재고", example = "77")
    val orderStock: Int? = null,
    @ApiModelProperty(value = "출고작업중 재고", example = "454")
    val shippingStock: Int? = null,
    @ApiModelProperty(value = "불량 재고", example = "77")
    val damagedStock: Int? = null,
    @ApiModelProperty(value = "반품 재고", example = "454")
    val returnStock: Int? = null,
    @ApiModelProperty(value = "보관 재고", example = "77")
    val keepingStock: Int? = null,
)

