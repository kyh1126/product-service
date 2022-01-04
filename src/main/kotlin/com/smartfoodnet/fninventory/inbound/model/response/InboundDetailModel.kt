package com.smartfoodnet.fninventory.inbound.model.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fninventory.inbound.entity.InboundExpectedDetail
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class InboundDetailModel(
    @ApiModelProperty(value = "id")
    val detailId: Long? = null,

    @ApiModelProperty(value = "실입고수량")
    val actualQuantity: Long? = null,

    @ApiModelProperty(value = "박스수")
    val boxQuantity: Long? = null,

    @ApiModelProperty(value = "팔레트수")
    val palletQuantity: Long? = null,

    @ApiModelProperty(value = "제조일자")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val manufactureDate: LocalDateTime? = null,

    @ApiModelProperty(value = "유통기한")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val expirationDate: LocalDateTime? = null,

    @ApiModelProperty(value = "실입고일자")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val actualInboundDate: LocalDateTime? = null
)