package com.smartfoodnet.apiclient.model

import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotNull

data class SfnMessage(
    @ApiModelProperty(value = "제목 (standard SNS 에만 사용됨)")
    val subject: String? = null,

    @NotNull
    @ApiModelProperty(value = "매세지 본문", required = true)
    val body: String
)
