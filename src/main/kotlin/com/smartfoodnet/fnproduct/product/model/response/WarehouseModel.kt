package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.product.entity.Warehouse
import io.swagger.annotations.ApiModelProperty

data class WarehouseModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "화주(고객사) ID")
    val partnerId: Long,

    @ApiModelProperty(value = "입고처이름")
    var name: String,
) {

    companion object {
        fun fromEntity(warehouse: Warehouse): WarehouseModel {
            return warehouse.run {
                WarehouseModel(
                    id = id,
                    partnerId = partnerId,
                    name = name
                )
            }
        }
    }
}
