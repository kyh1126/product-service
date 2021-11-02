package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.product.entity.PackageProductMapping
import io.swagger.annotations.ApiModelProperty

data class PackageProductMappingDetailModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "모음상품정보")
    var packageProductModel: BasicProductSimpleModel,

    @ApiModelProperty(value = "기본상품정보")
    var basicProductModel: BasicProductSimpleModel,

    @ApiModelProperty(value = "수량")
    var quantity: Int,
) {

    companion object {
        fun fromEntity(packageProductMapping: PackageProductMapping): PackageProductMappingDetailModel {
            return packageProductMapping.run {
                PackageProductMappingDetailModel(
                    id = id,
                    packageProductModel = BasicProductSimpleModel.fromEntity(packageProduct!!),
                    basicProductModel = BasicProductSimpleModel.fromEntity(selectedBasicProduct),
                    quantity = quantity,
                )
            }
        }
    }
}
