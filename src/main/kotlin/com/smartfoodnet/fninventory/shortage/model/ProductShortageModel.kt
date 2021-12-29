package com.smartfoodnet.fninventory.shortage.model

import io.swagger.annotations.ApiModelProperty

data class ProductShortageModel(
    @ApiModelProperty(value = "기본상품 ID")
    val basicProductId: Long? = null,

    @ApiModelProperty(value = "기본상품명")
    val basicProductName: String? = null,

    @ApiModelProperty(value = "기본상품 코드")
    val basicProductCode: String? = null,

    @ApiModelProperty(value = "결품 수량")
    val shortageCount: Int? = null,

    @ApiModelProperty(value = "결품 영향 주문 수")
    val shortageOrderCount: Int? = null,

    @ApiModelProperty(value = "결품 영향 주문 금약")
    val totalShortagePrice: Double? = null,

    @ApiModelProperty(value = "총 주문 수량")
    val totalOrderCount: Int? = null,

    @ApiModelProperty(value = "가용재고 수량")
    val availableStockCount: Int? = null
)