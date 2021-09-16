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

    @ApiModelProperty(value = "대분류")
    var level1: String = level1Category.keyName,

    @ApiModelProperty(value = "중분류")
    var level2: String? = level2Category?.keyName,
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
