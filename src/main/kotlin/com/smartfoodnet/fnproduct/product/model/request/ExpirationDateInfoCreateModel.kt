package com.smartfoodnet.fnproduct.product.model.request

import com.smartfoodnet.fnproduct.product.entity.ExpirationDateInfo
import io.swagger.annotations.ApiModelProperty

data class ExpirationDateInfoCreateModel(
    @ApiModelProperty(value = "제조일자기재여부", allowableValues = "Y,N")
    val manufactureDateWriteYn: String = "N",

    @ApiModelProperty(value = "유통기한기재여부", allowableValues = "Y,N")
    val expirationDateWriteYn: String = "N",

    @ApiModelProperty(value = "유통기한(제조일+X일)")
    val expirationDate: Int? = null,
) {
    fun toEntity(): ExpirationDateInfo {
        return ExpirationDateInfo(
            manufactureDateWriteYn = manufactureDateWriteYn,
            expirationDateWriteYn = expirationDateWriteYn,
            expirationDate = expirationDate
        )
    }
}
