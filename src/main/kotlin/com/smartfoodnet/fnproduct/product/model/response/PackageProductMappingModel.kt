package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.product.entity.PackageProductMapping
import io.swagger.annotations.ApiModelProperty

data class PackageProductMappingModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "모음상품 ID")
    var packageProductId: Long,

    @ApiModelProperty(value = "기본상품정보")
    var basicProductModel: BasicProductSimpleModel,

    @ApiModelProperty(value = "수량")
    var quantity: Int,
) {

    companion object {
        fun fromEntity(
            packageProductMapping: PackageProductMapping,
            selectedBasicProduct: BasicProductSimpleModel
        ): PackageProductMappingModel {
            return packageProductMapping.run {
                PackageProductMappingModel(
                    id = id,
                    packageProductId = packageProduct.id,
                    basicProductModel = selectedBasicProduct,
                    quantity = quantity,
                )
            }
        }
    }
}
