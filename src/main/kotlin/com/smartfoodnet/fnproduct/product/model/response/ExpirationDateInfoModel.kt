package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.product.entity.ExpirationDateInfo
import io.swagger.annotations.ApiModelProperty

data class ExpirationDateInfoModel(
    @ApiModelProperty(value = "제조일자기재여부", allowableValues = "Y,N")
    var manufactureDateWriteYn: String? = null,

    @ApiModelProperty(value = "유통기한기재여부", allowableValues = "Y,N")
    var expirationDateWriteYn: String? = null,

    @ApiModelProperty(value = "유통기한(제조일+X일)")
    var manufactureToExpirationDate: Int? = null,
) {

    companion object {
        fun fromEntity(expirationDateInfo: ExpirationDateInfo): ExpirationDateInfoModel {
            return expirationDateInfo.run {
                ExpirationDateInfoModel(
                    manufactureDateWriteYn = manufactureDateWriteYn,
                    expirationDateWriteYn = expirationDateWriteYn,
                    manufactureToExpirationDate = manufactureToExpirationDate,
                )
            }
        }
    }
}
