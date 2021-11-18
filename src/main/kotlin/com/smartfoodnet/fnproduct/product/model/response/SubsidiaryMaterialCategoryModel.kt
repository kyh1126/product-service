package com.smartfoodnet.fnproduct.product.model.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.fnproduct.code.entity.Code
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory
import io.swagger.annotations.ApiModelProperty

data class SubsidiaryMaterialCategoryModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @JsonIgnore
    val level1Category: Code,

    @JsonIgnore
    val level2Category: Code? = null,

    @ApiModelProperty(value = "대분류")
    val level1: String = level1Category.keyName,

    @ApiModelProperty(value = "소분류")
    val level2: String? = level2Category?.keyName,

    @ApiModelProperty(value = "수량적용여부", allowableValues = "Y,N")
    val quantityApplyYn: String,
) {

    companion object {
        fun fromEntity(category: SubsidiaryMaterialCategory): SubsidiaryMaterialCategoryModel {
            return category.run {
                SubsidiaryMaterialCategoryModel(
                    id = id,
                    level1Category = level1Category,
                    level2Category = level2Category,
                    quantityApplyYn = quantityApplyYn
                )
            }
        }
    }
}
