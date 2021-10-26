package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import io.swagger.annotations.ApiModelProperty

data class PackageProductDetailModel(
    @ApiModelProperty(value = "기본상품정보")
    var basicProduct: BasicProductSimpleModel,

    @ApiModelProperty(value = "모음상품(매핑)정보")
    var packageProducts: MutableList<PackageProductModel> = mutableListOf(),
) {

    companion object {
        fun fromEntity(basicProduct: BasicProduct): PackageProductDetailModel {
            return basicProduct.run {
                PackageProductDetailModel(
                    basicProduct = BasicProductSimpleModel.fromEntity(this),
                    packageProducts = packageProducts
                        .map { PackageProductModel.fromEntity(it) }.toMutableList(),
                )
            }
        }
    }
}
