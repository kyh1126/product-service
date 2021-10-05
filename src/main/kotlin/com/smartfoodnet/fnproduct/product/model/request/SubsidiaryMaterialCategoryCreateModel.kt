package com.smartfoodnet.fnproduct.product.model.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.fnproduct.code.entity.Code
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory
import io.swagger.annotations.ApiModelProperty

data class SubsidiaryMaterialCategoryCreateModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @JsonIgnore
    var level1Category: Code,

    @JsonIgnore
    var level2Category: Code? = null,

    @JsonIgnore
    var level3Category: Code? = null,

    @ApiModelProperty(value = "대분류")
    var level1: String = level1Category.keyName,

    @ApiModelProperty(value = "소분류")
    var level2: String? = level2Category?.keyName,

    @ApiModelProperty(value = "수량적용여부")
    var quantityApplyYn: String,
) {

    companion object {
        fun fromEntity(category: SubsidiaryMaterialCategory): SubsidiaryMaterialCategoryCreateModel {
            return category.run {
                SubsidiaryMaterialCategoryCreateModel(
                    id = id,
                    level1Category = level1Category,
                    level2Category = level2Category,
                    quantityApplyYn = quantityApplyYn
                )
            }
        }
    }
}
