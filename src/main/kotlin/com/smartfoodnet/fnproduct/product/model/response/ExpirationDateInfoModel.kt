package com.smartfoodnet.fnproduct.product.model.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.fnproduct.product.entity.ExpirationDateInfo
import io.swagger.annotations.ApiModelProperty

data class ExpirationDateInfoModel(
    @JsonIgnore
    var id: Long? = null,

    @JsonIgnore
    var basicProductId: Long? = null,

    @ApiModelProperty(value = "제조일자 기재 여부")
    var manufactureDateWriteYn: String? = null,

    @ApiModelProperty(value = "유통기한 기재 여부")
    var expirationDateWriteYn: String? = null,

    @ApiModelProperty(value = "유통기한(제조일+X일)")
    var expirationDate: Int? = null,
) {

    companion object {
        fun fromEntity(expirationDateInfo: ExpirationDateInfo): ExpirationDateInfoModel {
            return expirationDateInfo.run {
                ExpirationDateInfoModel(
                    id = id,
                    basicProductId = basicProduct!!.id!!,
                    manufactureDateWriteYn = manufactureDateWriteYn,
                    expirationDateWriteYn = expirationDateWriteYn,
                    expirationDate = expirationDate
                )
            }
        }
    }
}
