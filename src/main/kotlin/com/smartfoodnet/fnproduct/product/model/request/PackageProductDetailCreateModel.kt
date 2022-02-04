package com.smartfoodnet.fnproduct.product.model.request

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.PackageProductMapping
import io.swagger.annotations.ApiModelProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class PackageProductDetailCreateModel(
    @field:Valid
    @ApiModelProperty(value = "모음상품매핑정보")
    val packageProductMappingModels: MutableList<PackageProductMappingCreateModel> = mutableListOf(),
) {
    @NotNull
    @Valid
    @JsonUnwrapped
    lateinit var packageProductModel: BasicProductPackageCreateModel

    fun toEntity(
        code: String,
        packageProductMappings: Set<PackageProductMapping> = setOf()
    ): BasicProduct {
        return packageProductModel.toEntity(code)
            .apply { packageProductMappings.forEach(::addPackageProductMappings) }
    }
}
