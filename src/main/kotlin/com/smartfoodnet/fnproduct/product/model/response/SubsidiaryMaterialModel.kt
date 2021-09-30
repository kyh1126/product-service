package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterial
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOption

data class SubsidiaryMaterialModel(
    var id: Long? = null,

    var basicProductId: Long,

    var subsidiaryMaterialId: Long,

    var seasonalOption: SeasonalOption,

    var name: String,

    var quantity: Int,
) {

    companion object {
        fun fromEntity(subsidiaryMaterial: SubsidiaryMaterial): SubsidiaryMaterialModel {
            return subsidiaryMaterial.run {
                SubsidiaryMaterialModel(
                    id = id,
                    basicProductId = basicProduct.id!!,
                    subsidiaryMaterialId = subsidiaryMaterial.id!!,
                    seasonalOption = seasonalOption,
                    name = name,
                    quantity = quantity
                )
            }
        }
    }
}

