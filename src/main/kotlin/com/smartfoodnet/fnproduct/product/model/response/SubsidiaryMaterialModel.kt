package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterial
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOption

data class SubsidiaryMaterialModel(
    var id: Long? = null,

    var basicProductId: Long,

    var subsidiaryMaterial: BasicProductModel,

    var seasonalOption: SeasonalOption,

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
                    basicProductId = basicProduct!!.id!!,
                    subsidiaryMaterial = basicProductSub,
                    seasonalOption = seasonalOption,
                    quantity = quantity
                )
            }
        }
    }
}

