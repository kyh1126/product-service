package com.smartfoodnet.apiclient.request


import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(value = "입고예정등록 모델")
data class NosnosInboundCreateModel(
    @ApiModelProperty(value = "고객사 ID", example = "77", required = true)
    @JsonProperty("member_id")
    val memberId: Long,

    @JsonProperty("plan_date")
    @ApiModelProperty(value = "입고예정일", example = "20211210")
    val planDate: String,

    @ApiModelProperty(value = "입고예정메모")
    val memo: String? = null,

    @JsonProperty("plan_product_list")
    @ApiModelProperty(value = "입고예정 상품리스트")
    val planProductList: List<PlanProduct>
)