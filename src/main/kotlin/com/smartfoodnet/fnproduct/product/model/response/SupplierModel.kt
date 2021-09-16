package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.product.entity.Supplier
import io.swagger.annotations.ApiModelProperty

data class SupplierModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "화주(고객사) ID")
    val partnerId: Long,

    @ApiModelProperty(value = "이름")
    var name: String,
) {

    companion object {
        fun fromEntity(supplier: Supplier): SupplierModel {
            return supplier.run {
                SupplierModel(
                    id = id,
                    partnerId = partnerId,
                    name = name
                )
            }
        }
    }
}
