package com.smartfoodnet.fnproduct.product.model.response

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import io.swagger.annotations.ApiModelProperty

data class PackageProductDetailModel(
    @JsonUnwrapped
    var packageProductModel: BasicProductSimpleModel,

    @ApiModelProperty(value = "모음상품매핑정보")
    var packageProductMappingModels: MutableList<PackageProductMappingModel> = mutableListOf(),
) {

    companion object {
        fun fromEntity(
            packageProduct: BasicProduct,
            basicProductById: Map<Long, BasicProduct>,
        ): PackageProductDetailModel {
            return packageProduct.run {
                PackageProductDetailModel(
                    packageProductModel = BasicProductSimpleModel.fromEntity(this),
                    packageProductMappingModels = packageProductMappings
                        .map {
                            val selectedBasicProduct =
                                BasicProductSimpleModel.fromEntity(basicProductById[it.selectedBasicProduct.id]!!)
                            PackageProductMappingModel.fromEntity(it, selectedBasicProduct)
                        }.toMutableList(),
                )
            }
        }
    }
}
