package com.smartfoodnet.fnproduct.product.model.request

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialMapping
import com.smartfoodnet.fnproduct.warehouse.entity.InWarehouse
import io.swagger.annotations.ApiModelProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class BasicProductDetailCreateModel(
    @field:Valid
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
        subsidiaryMaterialMappings: Set<SubsidiaryMaterialMapping> = setOf(),
        inWarehouse: InWarehouse,
    ): BasicProduct {
        val basicProduct = basicProductModel.toEntity(
            code,
            basicProductCategory,
            subsidiaryMaterialCategory,
            inWarehouse
        )
        return basicProduct.apply { subsidiaryMaterialMappings.forEach(this::addSubsidiaryMaterialMappings) }
    }
}
