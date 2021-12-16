package com.smartfoodnet.nosnos.api.inventory.model.request


import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(value = "입고예정등록 모델")
data class NosnosInboundCreateModel(
    @ApiModelProperty(value = "고객사 ID", example = "77", required = true)
    @JsonProperty("member_id")
    val memberId: Long,

    @JsonProperty("receiving_plan_code")
    @ApiModelProperty(value = "입고예정코드")
    val receivingPlanCode: String? = null,

    @JsonProperty("plan_date")
    @ApiModelProperty(value = "입고예정일", example = "20211210")
    val planDate: String,

    @ApiModelProperty(value = "입고예정메모")
    val memo: String? = null,

    @JsonProperty("add_info1")
    @ApiModelProperty(value = "추가정보1")
    val addInfo1: String? = null,

    @JsonProperty("add_info2")
    @ApiModelProperty(value = "추가정보2")
    val addInfo2: String? = null,

    @JsonProperty("add_info3")
    @ApiModelProperty(value = "추가정보3")
    val addInfo3: String? = null,

    @JsonProperty("add_info4")
    @ApiModelProperty(value = "추가정보4")
    val addInfo4: String? = null,

    @JsonProperty("add_info5")
    @ApiModelProperty(value = "추가정보5")
    val addInfo5: String? = null,

    @JsonProperty("plan_product_list")
    @ApiModelProperty(value = "입고예정 상품리스트")
    val planProductList: List<PlanProduct>
)