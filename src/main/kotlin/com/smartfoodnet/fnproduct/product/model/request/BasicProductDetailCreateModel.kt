package com.smartfoodnet.fnproduct.product.model.request

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.product.entity.*
import io.swagger.annotations.ApiModelProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class BasicProductDetailCreateModel(
    @Valid
    @ApiModelProperty(value = "부자재매핑정보")
    val subsidiaryMaterialMappingModels: MutableList<SubsidiaryMaterialMappingCreateModel> = mutableListOf(),
) {
    @NotNull
    @Valid
    @JsonUnwrapped
    lateinit var basicProductModel: BasicProductCreateModel

    fun toEntity(
        code: String? = null,
        basicProductCategory: BasicProductCategory? = null,
        subsidiaryMaterialCategory: SubsidiaryMaterialCategory? = null,
        expirationDateInfo: ExpirationDateInfo? = null,
        subsidiaryMaterialMappings: Set<SubsidiaryMaterialMapping> = setOf(),
        warehouse: Warehouse,
    ): BasicProduct {
        val basicProduct = basicProductModel.toEntity(
            code,
            basicProductCategory,
            subsidiaryMaterialCategory,
            expirationDateInfo,
            warehouse
        )
        return basicProduct.apply { subsidiaryMaterialMappings.forEach(this::addSubsidiaryMaterialMappings) }
    }
}
