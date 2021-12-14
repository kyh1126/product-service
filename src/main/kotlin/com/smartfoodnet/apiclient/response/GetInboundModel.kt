package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
@ApiModel(value = "입고등록조회 모델")
data class GetInboundModel(
    @ApiModelProperty(value = "입고예정번호", example = "454")
    val receivingPlanId: Long,
    @ApiModelProperty(value = "고객사 ID", example = "77")
    val memberId: Long,
    @ApiModelProperty(value = "입고예정코드", example = "L002-20211213-0001")
    val receivingPlanCode: String? = null,
    @ApiModelProperty(value = "입고예정일", example = "20211210")
    val planDate: String,
    @ApiModelProperty(value = "진행상태", example = "1")
    val plan_status: Long,
    @ApiModelProperty(value = "완료일", example = "20211211")
    val complete_dt: String,
    @ApiModelProperty(value = "입고예정메모", example = "MEMO")
    val memo: String? = null,
    @ApiModelProperty(value = "추가정보1")
    val addInfo1: String? = null,
    @ApiModelProperty(value = "추가정보2")
    val addInfo2: String? = null,
    @ApiModelProperty(value = "추가정보3")
    val addInfo3: String? = null,
    @ApiModelProperty(value = "추가정보4")
    val addInfo4: String? = null,
    @ApiModelProperty(value = "추가정보5")
    val addInfo5: String? = null,
    @ApiModelProperty(value = "입고예정 상품리스트")
    val planProductList: List<PlanProduct>
)