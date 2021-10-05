package com.smartfoodnet.fnproduct.product.model.request

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterial
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOption

data class SubsidiaryMaterialCreateModel(
    var id: Long? = null,

    var basicProductId: Long,

    var subsidiaryMaterialId: Long,

    var seasonalOption: SeasonalOption,

    var name: String,

    var quantity: Int,
) {
    fun toEntity(basicProduct: BasicProduct, subsidiaryMaterial: BasicProduct): SubsidiaryMaterial {
        return SubsidiaryMaterial(
            basicProduct = basicProduct,
            subsidiaryMaterial = subsidiaryMaterial,
            seasonalOption = seasonalOption,
            name = name,
            quantity = quantity
        )
    }

}

