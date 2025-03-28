package com.smartfoodnet.fnproduct.product.model.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class BasicProductSimpleModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(
        value = "구분 (BASIC:기본상품/CUSTOM_SUB:고객전용부자재/SUB:공통부자재/PACKAGE:모음상품)",
        allowableValues = "BASIC,CUSTOM_SUB,SUB,PACKAGE"
    )
    var type: BasicProductType,

    @ApiModelProperty(value = "화주(고객사) ID")
    val partnerId: Long?,

    @ApiModelProperty(value = "상품명")
    var name: String?,

    @ApiModelProperty(value = "상품코드")
    var code: String? = null,

    @ApiModelProperty(value = "상품바코드기재여부", allowableValues = "Y,N")
    var barcodeYn: String? = null,

    @ApiModelProperty(value = "상품바코드")
    var barcode: String? = null,

    @ApiModelProperty(
        value = "취급온도 (ROOM:상온/REFRIGERATE:냉장/FREEZE:냉동/MIX:혼합)",
        allowableValues = "ROOM,REFRIGERATE,FREEZE,MIX"
    )
    var handlingTemperature: HandlingTemperatureType? = null,

    @ApiModelProperty(value = "활성화여부", allowableValues = "Y,N")
    val activeYn: String,

    @ApiModelProperty(value = "생성시간")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val createdAt: LocalDateTime? = null,

    @ApiModelProperty(value = "업데이트시간")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val updatedAt: LocalDateTime? = null,
) {

    companion object {
        fun fromEntity(basicProduct: BasicProduct): BasicProductSimpleModel {
            return basicProduct.run {
                BasicProductSimpleModel(
                    id = id,
                    type = type,
                    partnerId = partnerId,
                    name = name,
                    code = code,
                    barcodeYn = barcodeYn,
                    barcode = barcode,
                    handlingTemperature = handlingTemperature,
                    activeYn = activeYn,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
            }
        }
    }
}
