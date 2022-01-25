package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.code.entity.Code
import com.smartfoodnet.fnproduct.product.model.dto.CategoryDto
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import io.swagger.annotations.ApiModelProperty

data class CategoryByLevelModel(
    @ApiModelProperty(value = "분류 id")
    val value: Long? = null,

    @ApiModelProperty(value = "분류명")
    val label: String? = null,

    @ApiModelProperty(value = "해당 분류 이하의 분류 모델 목록")
    var children: Set<CategoryDto>,
) {

    companion object {
        fun fromEntity(category: Code, children: List<CategoryDto?>): CategoryByLevelModel {
            return category.run {
                // level1 만으로는 카테고리 id 가 없으므로 value 를 설정하지 않는다.
                CategoryByLevelModel(
                    label = keyName,
                    children = children.filterNotNull().toSet()
                )
            }
        }

        fun fromModel(children: List<BasicProductModel>): CategoryByLevelModel {
            return CategoryByLevelModel(
                label = BasicProductType.CUSTOM_SUB.desc,
                children = children.map { CategoryDto.from(it.id, it.name) }.toSet()
            )
        }
    }
}
