package com.smartfoodnet.apiclient.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.swagger.annotations.ApiModelProperty

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
class InboundWorkReadModel(
    @ApiModelProperty(value = "고객사ID", required = true, example = "42")
    val memberId : Long,
    @ApiModelProperty(value = "작업시작일", required = true, example = "20220104")
    val startDt : String,
    @ApiModelProperty(value = "작업종료일", required = true, example = "20220105")
    val endDt : String,
    @ApiModelProperty(value = "작업구분")
    val workType: Int? = null,
    @ApiModelProperty(value = "입고분류")
    val receivingType: Int? = null,
    @ApiModelProperty(value = "입고예정 ID")
    val receivingPlanId : Long?= null,
    @ApiModelProperty(value = "출고상품 ID")
    val shippingProductIds: List<Long>? = null,
    @ApiModelProperty(value = "페이지번호", example = "1")
    var page: Int = 1
)