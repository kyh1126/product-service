package com.smartfoodnet.fnproduct.product.model.dto

import com.smartfoodnet.fnproduct.code.entity.Code

data class CategoryDto(
    var value: Long? = null,
    var label: String? = null,
) {

    companion object {
        fun fromEntity(categoryId: Long?, category: Code?): CategoryDto? {
            return category?.run {
                CategoryDto(value = categoryId, label = keyName)
            }
        }

        fun from(categoryId: Long?, name: String?): CategoryDto {
            return CategoryDto(value = categoryId, label = name)
        }
    }
}

