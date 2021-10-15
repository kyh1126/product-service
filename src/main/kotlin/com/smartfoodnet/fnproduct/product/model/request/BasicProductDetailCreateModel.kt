package com.smartfoodnet.fnproduct.product.model.request

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.product.entity.*
import io.swagger.annotations.ApiModelProperty

data class BasicProductDetailCreateModel(
    @ApiModelProperty(value = "부자재(매핑)정보")
    val subsidiaryMaterialModels: MutableList<SubsidiaryMaterialCreateModel> = mutableListOf(),
) {
    @JsonUnwrapped
    lateinit var basicProductModel: BasicProductCreateModel

    fun toEntity(
        code: String? = null,
        basicProductCategory: BasicProductCategory? = null,
        subsidiaryMaterialCategory: SubsidiaryMaterialCategory? = null,
        expirationDateInfo: ExpirationDateInfo? = null,
        subsidiaryMaterials: MutableList<SubsidiaryMaterial> = mutableListOf(),
        warehouse: Warehouse,
    ): BasicProduct {
        val basicProduct = basicProductModel.toEntity(
            code,
            basicProductCategory,
            subsidiaryMaterialCategory,
            expirationDateInfo,
            warehouse
        )
        return basicProduct.apply { subsidiaryMaterials.forEach(this::addSubsidiaryMaterials) }
    }
}
