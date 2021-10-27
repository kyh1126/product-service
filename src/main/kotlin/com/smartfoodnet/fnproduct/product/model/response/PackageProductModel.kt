package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.product.entity.PackageProduct
import io.swagger.annotations.ApiModelProperty

data class PackageProductModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "기본상품 ID")
    var basicProductId: Long,

    @ApiModelProperty(value = "모음상품정보")
    var packageProduct: BasicProductSimpleModel,

    @ApiModelProperty(value = "수량")
    var quantity: Int,
) {

    companion object {
        fun fromEntity(packageProduct: PackageProduct): PackageProductModel {
            return packageProduct.run {
                PackageProductModel(
                    id = id,
                    basicProductId = this.packageProduct!!.id!!,
                    packageProduct = BasicProductSimpleModel.fromEntity(this.selectedBasicProduct),
                    quantity = quantity,
                )
            }
        }
    }
}
