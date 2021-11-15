package com.smartfoodnet.fnproduct.product.model.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.fnproduct.code.entity.Code
import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory
import io.swagger.annotations.ApiModelProperty

data class BasicProductCategoryModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @JsonIgnore
    var level1Category: Code,

    @JsonIgnore
    var level2Category: Code? = null,

    @ApiModelProperty(value = "대분류 / 중분류")
    var level: String = listOf(level1Category.keyName, level2Category?.keyName).joinToString(" / "),
) {

    companion object {
        fun fromEntity(category: BasicProductCategory): BasicProductCategoryModel {
            return category.run {
                BasicProductCategoryModel(
                    id = id,
                    level1Category = level1Category,
                    level2Category = level2Category
                )
            }
        }
    }
}
