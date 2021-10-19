package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterial
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOption
import io.swagger.annotations.ApiModelProperty

data class SubsidiaryMaterialModel(
    @ApiModelProperty(value = "id")
    var id: Long? = null,

    @ApiModelProperty(value = "기본상품 ID")
    var basicProductId: Long? = null,

    @ApiModelProperty(value = "부자재(기본상품) 정보")
    var subsidiaryMaterial: BasicProductModel,

    @ApiModelProperty(value = "계절옵션 (ALL:계절무관/SUMMER:하절기)", allowableValues = "ALL,SUMMER")
    var seasonalOption: SeasonalOption,

    @ApiModelProperty(value = "수량")
    var quantity: Int,
) {

    companion object {
        fun fromEntity(
            subsidiaryMaterial: SubsidiaryMaterial,
            basicProductSub: BasicProductModel,
        ): SubsidiaryMaterialModel {
            return subsidiaryMaterial.run {
                SubsidiaryMaterialModel(
                    id = id,
                    basicProductId = basicProduct?.id,
                    subsidiaryMaterial = basicProductSub,
                    seasonalOption = seasonalOption,
                    quantity = quantity
                )
            }
        }
    }
}
