package com.smartfoodnet.fninventory.stock.model

import com.smartfoodnet.fninventory.stock.entity.MoveEventType
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class StockMoveEventModel(
    @ApiModelProperty(value = "기본상품 ID")
    val basicProductId: Long? = null,

    @ApiModelProperty(value = "기본상품명")
    val basicProductName: String? = null,

    @ApiModelProperty(value = "기본상품 코드")
    val basicProductCode: String? = null,

    @ApiModelProperty(value = "출고상품 ID")
    val shippingProductId: Long? = null,

    @ApiModelProperty(value = "상품 바코드")
    val barcode: String? = null,

    @ApiModelProperty(value = "유통기한관리여부")
    val expirationDateManagementYn: String? = null,

    @ApiModelProperty(value = "수량")
    var moveQuantity: Int? = null,

    @ApiModelProperty(value = "이벤트")
    var moveEventType: MoveEventType? = null,

    @ApiModelProperty(value = "가용재고")
    var availableStockCount: Int? = null,

    @ApiModelProperty(value = "총재고")
    var totalStockCount: Int? = null,

    @ApiModelProperty(value = "처리일자")
    var processedAt: LocalDateTime? = null,

)