package com.smartfoodnet.fnproduct.order.model

import io.swagger.annotations.ApiModelProperty
import springfox.documentation.annotations.ApiIgnore

data class BasicProductAddModel(
    @ApiModelProperty(value = "화주(고객)사 ID", example = "11")
    var partnerId: Long,
    @ApiModelProperty(hidden = true)
    var collectedOrderId: Long,
    val basicProducts: List<BasicProductMappedModel>
)

data class BasicProductMappedModel(
    @ApiModelProperty(value = "기본 상품 ID")
    val basicProductId: Long,
    @ApiModelProperty(value = "매칭 수량", example = "1")
    val quantity: Int
)

class ConfirmProductAddModel(
    @ApiModelProperty(value = "화주(고객)사 ID", example = "11", hidden = true)
    var partnerId: Long,
    val confirmProductList: List<ConfirmProductMappedModel>
)

class ConfirmProductMappedModel(
    var collectedOrderId: Long,
    val basicProducts: List<BasicProductMappedModel>
)