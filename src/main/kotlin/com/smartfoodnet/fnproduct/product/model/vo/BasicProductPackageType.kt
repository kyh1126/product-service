package com.smartfoodnet.fnproduct.product.model.vo

enum class BasicProductPackageType(val text: String, val singlePackagingYn: String) {
    SINGLE("단수", "Y"),
    NORMAL("일반", "N");

    companion object {
        fun fromSinglePackagingYn(yn: String): BasicProductPackageType {
            return values().firstOrNull { it.singlePackagingYn == yn }
                ?: throw IllegalArgumentException("Format $yn is illegal")
        }
    }
}
