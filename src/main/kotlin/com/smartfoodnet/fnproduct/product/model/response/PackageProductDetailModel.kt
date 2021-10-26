package com.smartfoodnet.fnproduct.product.model.response

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import io.swagger.annotations.ApiModelProperty

data class PackageProductDetailModel(
    @JsonUnwrapped
    var basicProductModel: BasicProductSimpleModel,

    @ApiModelProperty(value = "모음상품(매핑)정보")
    var packageProductModels: MutableList<PackageProductModel> = mutableListOf(),
) {

    companion object {
        fun fromEntity(basicProduct: BasicProduct): PackageProductDetailModel {
            return basicProduct.run {
                PackageProductDetailModel(
                    basicProductModel = BasicProductSimpleModel.fromEntity(this),
                    packageProductModels = packageProducts
                        .map { PackageProductModel.fromEntity(it) }.toMutableList(),
                )
            }
        }
    }
}
