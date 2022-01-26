package com.smartfoodnet.fnproduct.product.model.request

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotNull

class BasicProductSimpleCreateModel {
    @ApiModelProperty(value = "id")
    val id: Long? = null

    @NotNull
    @ApiModelProperty(
        value = "구분 (BASIC:기본상품/CUSTOM_SUB:고객전용부자재/SUB:공통부자재/PACKAGE:모음상품)",
        allowableValues = "BASIC,CUSTOM_SUB,SUB,PACKAGE"
    )
    val type: BasicProductType? = null

    @ApiModelProperty(value = "화주(고객사) ID")
    val partnerId: Long? = null

    @ApiModelProperty(value = "화주(고객사) 코드")
    val partnerCode: String? = null

    @ApiModelProperty(value = "상품명")
    val name: String? = null

    @ApiModelProperty(value = "상품코드")
    val code: String? = null

    @ApiModelProperty(value = "상품바코드기재여부", allowableValues = "Y,N")
    val barcodeYn: String = "N"

    @ApiModelProperty(value = "상품바코드")
    val barcode: String? = null

    @ApiModelProperty(
        value = "취급온도 (ROOM:상온/REFRIGERATE:냉장/FREEZE:냉동/MIX:혼합)",
        allowableValues = "ROOM,REFRIGERATE,FREEZE,MIX"
    )
    val handlingTemperature: HandlingTemperatureType? = null

    @ApiModelProperty(value = "활성화여부 (default: Y)", allowableValues = "Y,N")
    val activeYn: String = "Y"

    fun toEntity(code: String?): BasicProduct {
        return BasicProduct(
            type = type!!,
            partnerId = partnerId,
            name = name,
            code = code,
            barcodeYn = barcodeYn,
            barcode = barcode,
            handlingTemperature = handlingTemperature,
            activeYn = activeYn,
        )
    }
}
