package com.smartfoodnet.fnproduct.product.model.request

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterial
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOption

data class SubsidiaryMaterialCreateModel(
    val id: Long? = null,

    val basicProductId: Long?,

    val subsidiaryMaterialId: Long,

    val seasonalOption: SeasonalOption,

    val name: String,

    val quantity: Int,
) {
    fun toEntity(subsidiaryMaterial: BasicProduct): SubsidiaryMaterial {
        return SubsidiaryMaterial(
            subsidiaryMaterial = subsidiaryMaterial,
            seasonalOption = seasonalOption,
            name = name,
            quantity = quantity
        )
    }
}

