package com.smartfoodnet.fnproduct.product.model.dto

import com.smartfoodnet.fnproduct.code.entity.Code

data class CategoryDto(
    var value: Long? = null,
    var label: String? = null,
) {

    companion object {
        fun fromEntity(category: Code?): CategoryDto? {
            return category?.run {
                CategoryDto(
                    value = id,
                    label = keyName
                )
            }
        }
    }
}

