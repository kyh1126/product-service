package com.smartfoodnet.fnproduct.product.model.request

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.product.entity.*
import io.swagger.annotations.ApiModelProperty

data class BasicProductDetailCreateModel(
    @JsonUnwrapped
    var basicProductModel: BasicProductCreateModel,

    @ApiModelProperty(value = "부자재정보")
    var subsidiaryMaterialModels: List<SubsidiaryMaterialCreateModel> = mutableListOf(),
) {
    fun toEntity(
        code: String,
        basicProductCategory: BasicProductCategory?,
        subsidiaryMaterialCategory: SubsidiaryMaterialCategory?,
        subsidiaryMaterial: SubsidiaryMaterial,
        warehouse: Warehouse,
    ): BasicProduct {


        return basicProductModel.toEntity(code, basicProductCategory, subsidiaryMaterialCategory, warehouse)
//            .apply {
//                subsidiaryMaterialModels.map { it.toEntity(this, subsidiaryMaterial) }
//                    .forEach { addSubsidiaryMaterials(it) }
//            }// TODO: 부자재 매핑정보 추가 필요
    }
}
