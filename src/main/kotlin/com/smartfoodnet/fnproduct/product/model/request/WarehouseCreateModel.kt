package com.smartfoodnet.fnproduct.product.model.request

import io.swagger.annotations.ApiModelProperty

data class WarehouseCreateModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "화주(고객사) ID")
    val partnerId: Long,

    @ApiModelProperty(value = "입고처코드")
    var code: Int,

    @ApiModelProperty(value = "입고처이름")
    var name: String,
)
