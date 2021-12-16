package com.smartfoodnet.fnproduct.stock.model

import io.swagger.annotations.ApiModelProperty

data class StockModel (
    @ApiModelProperty(value = "기본상품 ID")
    val basicProductId: Long? = null,

    @ApiModelProperty(value = "기본상품명")
    val basicProductName: String? = null,

    @ApiModelProperty(value = "기본상품 코드")
    val basicProductCode: String? = null,

    @ApiModelProperty(value = "상품 바코드")
    val barcode: String? = null,

    @ApiModelProperty(value = "유통기한 관리 여부")
    val expirationDateManagementYn: String? = null,

    @ApiModelProperty(value = "총재고")
    val totalStockCount: Int? = null,

    @ApiModelProperty(value = "가용재고")
    val normalStockCount: Int? = null,

    @ApiModelProperty(value = "점유 PLT 수")
    val occupiedPLTCount: Int? = null,
)