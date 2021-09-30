package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.product.entity.ExpirationDateInfo
import io.swagger.annotations.ApiModelProperty

data class ExpirationDateInfoModel(
    @ApiModelProperty(value = "id")
    var id: Long? = null,

    @ApiModelProperty(value = "기본상품 ID")
    var basicProductId: Long,

    @ApiModelProperty(value = "제조일자 기재 여부")
    var manufactureDateWriteYn: String,

    @ApiModelProperty(value = "유통기한 기재 여부")
    var expirationDateWriteYn: String,

    @ApiModelProperty(value = "유통기한(제조일+X일)")
    var expirationDate: Int,
) {

    companion object {
        fun fromEntity(expirationDateInfo: ExpirationDateInfo): ExpirationDateInfoModel {
            return expirationDateInfo.run {
                ExpirationDateInfoModel(
                    id = id,
                    basicProductId = basicProduct.id!!,
                    manufactureDateWriteYn = manufactureDateWriteYn,
                    expirationDateWriteYn = expirationDateWriteYn,
                    expirationDate = expirationDate
                )
            }
        }
    }
}