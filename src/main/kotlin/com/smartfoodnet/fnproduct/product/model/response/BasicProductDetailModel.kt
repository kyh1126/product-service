package com.smartfoodnet.fnproduct.product.model.response

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import io.swagger.annotations.ApiModelProperty

data class BasicProductDetailModel(
    @JsonUnwrapped
    var basicProductModel: BasicProductModel,

    @ApiModelProperty(value = "부자재(매핑)정보")
    var subsidiaryMaterialModels: List<SubsidiaryMaterialModel> = mutableListOf(),
) {

    companion object {
        fun fromEntity(
            basicProduct: BasicProduct,
            subsidiaryMaterialById: Map<Long?, BasicProduct>,
        ): BasicProductDetailModel {
            return basicProduct.run {
                BasicProductDetailModel(
                    basicProductModel = BasicProductModel.fromEntity(this),
                    subsidiaryMaterialModels = subsidiaryMaterials.map {
                        val basicProductSub =
                            BasicProductModel.fromEntity(subsidiaryMaterialById[it.subsidiaryMaterial.id]!!)
                        SubsidiaryMaterialModel.fromEntity(it, basicProductSub)
                    },
                )
            }
        }
    }
}
