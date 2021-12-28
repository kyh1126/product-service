package com.smartfoodnet.apiclient.request

import io.swagger.annotations.ApiModelProperty

data class BasicProductCreatedModel(
    @ApiModelProperty(value = "기본상품 ID")
    val basicProductId: Long,
)
