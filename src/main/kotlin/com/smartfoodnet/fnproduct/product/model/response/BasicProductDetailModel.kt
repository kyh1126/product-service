package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import io.swagger.annotations.ApiModelProperty

data class BasicProductDetailModel(
    @ApiModelProperty(value = "기초정보")
    var basicProductModel: BasicProductModel,

    @ApiModelProperty(value = "부자재정보")
    var subsidiaryMaterialModels: List<SubsidiaryMaterialModel> = mutableListOf(),
) {

    companion object {
        fun fromEntity(basicProduct: BasicProduct): BasicProductDetailModel {
            return basicProduct.run {
                BasicProductDetailModel(
                    basicProductModel = BasicProductModel.fromEntity(this),
                    subsidiaryMaterialModels = subsidiaryMaterials.map {
                        SubsidiaryMaterialModel.fromEntity(it)
                    },
                )
            }
        }
    }
}
