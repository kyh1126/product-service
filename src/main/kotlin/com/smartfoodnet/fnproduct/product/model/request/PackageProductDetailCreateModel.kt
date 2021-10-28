package com.smartfoodnet.fnproduct.product.model.request

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.PackageProductMapping
import io.swagger.annotations.ApiModelProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class PackageProductDetailCreateModel(
    @Valid
    @ApiModelProperty(value = "모음상품(매핑)정보")
    val packageProductModels: MutableList<PackageProductCreateModel> = mutableListOf(),
) {
    @NotNull
    @Valid
    @JsonUnwrapped
    lateinit var basicProductModel: BasicProductSimpleCreateModel

    fun toEntity(
        code: String? = null,
        packageProductMappings: Set<PackageProductMapping> = setOf()
    ): BasicProduct {
        return basicProductModel.toEntity(code)
            .apply { packageProductMappings.forEach(this::addPackageProductMappings) }
    }
}
