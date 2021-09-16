package com.smartfoodnet.fnproduct.product.model.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.fnproduct.code.entity.Code
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory
import io.swagger.annotations.ApiModelProperty

data class SubsidiaryMaterialCategoryModel(
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

    @ApiModelProperty(value = "중분류")
    var level2: String? = level2Category?.keyName,

    @ApiModelProperty(value = "소분류")
    var level3: String? = level3Category?.keyName,
) {

    companion object {
        fun fromEntity(category: SubsidiaryMaterialCategory): SubsidiaryMaterialCategoryModel {
            return category.run {
                SubsidiaryMaterialCategoryModel(
                    id = id,
                    level1Category = level1Category,
                    level2Category = level2Category,
                    level3Category = level3Category
                )
            }
        }
    }
}
