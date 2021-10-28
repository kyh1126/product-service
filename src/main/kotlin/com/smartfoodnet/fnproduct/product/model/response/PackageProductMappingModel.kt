package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.product.entity.PackageProductMapping
import io.swagger.annotations.ApiModelProperty

data class PackageProductMappingModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "모음상품정보")
    var packageProduct: BasicProductSimpleModel,

    @ApiModelProperty(value = "기본상품정보")
    var basicProduct: BasicProductSimpleModel,

    @ApiModelProperty(value = "수량")
    var quantity: Int,
) {

    companion object {
        fun fromEntity(packageProductMapping: PackageProductMapping): PackageProductMappingModel {
            return packageProductMapping.run {
                PackageProductMappingModel(
                    id = id,
                    packageProduct = BasicProductSimpleModel.fromEntity(packageProduct!!),
                    basicProduct = BasicProductSimpleModel.fromEntity(selectedBasicProduct),
                    quantity = quantity,
                )
            }
        }
    }
}
