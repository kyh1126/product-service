package com.smartfoodnet.fnproduct.partner.model.response

import com.smartfoodnet.fnproduct.product.entity.Partner
import io.swagger.annotations.ApiModelProperty

data class PartnerModel(
    @ApiModelProperty(value = "id")
    var id: Long? = null,

    @ApiModelProperty(value = "이름")
    var name: String,

    @ApiModelProperty(value = "고객번호")
    var customerNumber: String,
) {

    companion object {
        fun fromEntity(partner: Partner): PartnerModel {
            return partner.run {
                PartnerModel(
                    id = id,
                    name = name,
                    customerNumber = customerNumber
                )
            }
        }
    }
}
