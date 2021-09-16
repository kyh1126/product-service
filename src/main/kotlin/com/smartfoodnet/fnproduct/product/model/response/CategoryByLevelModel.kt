package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.code.entity.Code
import com.smartfoodnet.fnproduct.product.model.dto.BasicProductCategoryDto
import io.swagger.annotations.ApiModelProperty

data class CategoryByLevelModel(
    @ApiModelProperty(value = "분류 id")
    val id: Long? = null,

    @ApiModelProperty(value = "분류명")
    val label: String? = null,

    @ApiModelProperty(value = "해당 분류 이하의 분류 모델 목록")
    var children: Set<BasicProductCategoryDto>,
) {

    companion object {
        fun fromEntity(category: Code, children: List<Code?>): CategoryByLevelModel {
            return category.run {
                CategoryByLevelModel(
                    id = id,
                    label = keyName,
                    children = children.mapNotNull { BasicProductCategoryDto.fromEntity(it) }.toSet()
                )
            }
        }
    }
}
