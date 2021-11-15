package com.smartfoodnet.fnproduct.product.model.request

import com.smartfoodnet.fnproduct.product.entity.ExpirationDateInfo
import com.sun.istack.NotNull
import io.swagger.annotations.ApiModelProperty

data class ExpirationDateInfoCreateModel(
    @NotNull
    @ApiModelProperty(value = "제조일자 기재 여부")
    var manufactureDateWriteYn: String,

    @NotNull
    @ApiModelProperty(value = "유통기한 기재 여부")
    var expirationDateWriteYn: String,

    @ApiModelProperty(value = "유통기한(제조일+X일)")
    var expirationDate: Int? = null,
) {
    fun toEntity(): ExpirationDateInfo {
        return ExpirationDateInfo(
            manufactureDateWriteYn = manufactureDateWriteYn,
            expirationDateWriteYn = expirationDateWriteYn,
            expirationDate = expirationDate
        )
    }
}
