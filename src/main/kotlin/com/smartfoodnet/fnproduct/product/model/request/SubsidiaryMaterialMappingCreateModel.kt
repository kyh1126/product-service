package com.smartfoodnet.fnproduct.product.model.request

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialMapping
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOption
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotNull

data class SubsidiaryMaterialMappingCreateModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "기본상품 ID")
    val basicProductId: Long?,

    @NotNull
    @ApiModelProperty(value = "부자재(기본상품) 정보")
    val subsidiaryMaterial: BasicProductCreateModel,

    @NotNull
    @ApiModelProperty(value = "계절옵션 (ALL:계절무관/SUMMER:하절기)", allowableValues = "ALL,SUMMER")
    val seasonalOption: SeasonalOption,

    @NotNull
    @ApiModelProperty(value = "수량")
    val quantity: Int,
) {
    fun toEntity(subsidiaryMaterial: BasicProduct): SubsidiaryMaterialMapping {
        return SubsidiaryMaterialMapping(
            subsidiaryMaterial = subsidiaryMaterial,
            seasonalOption = seasonalOption,
            quantity = quantity
        )
    }
}
