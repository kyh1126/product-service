package com.smartfoodnet.fnproduct.order.model

import io.swagger.annotations.ApiModelProperty

data class BasicProductAddModel(
    @ApiModelProperty(value = "화주(고객)사 ID", example = "11")
    val partnerId : Long,
    val basicProducts : List<BasicProductMappedModel>
)

data class BasicProductMappedModel(
    @ApiModelProperty(value = "기본 상품 ID")
    val basicProductId : Long,
    @ApiModelProperty(value = "매칭 수량", example = "1")
    val quantity : Int
)