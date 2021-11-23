package com.smartfoodnet.fnproduct.order.model

import io.swagger.annotations.ApiModelProperty

class SenderModel (
    @ApiModelProperty(value = "보내는 분 이름")
    var name: String? = null,
    @ApiModelProperty(value = "보내는 분 주소")
    var address: String? = null,
    @ApiModelProperty(value = "보내는 분 전화번호")
    var phoneNumber: String? = null,
)