package com.smartfoodnet.fnproduct.product.model.dto

import com.smartfoodnet.fnproduct.code.entity.Code

data class BasicProductCategoryDto(
    var value: Long? = null,
    var label: String? = null,
) {

    companion object {
        fun fromEntity(category: Code?): BasicProductCategoryDto? {
            return category?.run {
                BasicProductCategoryDto(
                    value = id,
                    label = keyName
                )
            }
        }
    }
}

