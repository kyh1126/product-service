package com.smartfoodnet.fnproduct.product.model.request

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterial
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOption
import io.swagger.annotations.ApiModelProperty

data class SubsidiaryMaterialCreateModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "기본상품 ID")
    val basicProductId: Long?,

    @ApiModelProperty(value = "부자재(기본상품) 정보")
    val subsidiaryMaterial: BasicProductCreateModel,

    @ApiModelProperty(value = "계절옵션 (ALL:계절무관/SUMMER:하절기)", allowableValues = "ALL,SUMMER")
    val seasonalOption: SeasonalOption,

    @ApiModelProperty(value = "수량")
    val quantity: Int,
) {
    fun toEntity(subsidiaryMaterial: BasicProduct): SubsidiaryMaterial {
        return SubsidiaryMaterial(
            subsidiaryMaterial = subsidiaryMaterial,
            seasonalOption = seasonalOption,
            quantity = quantity
        )
    }
}
