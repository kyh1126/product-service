package com.smartfoodnet.fnproduct.product.model.vo

enum class BasicProductType(val desc: String, val code: String?) {
    BASIC("기본상품", "A"),
    CUSTOM_SUB("고객전용부자재", "C"),
    SUB("공통부자재", null),
    PACKAGE("모음상품", "B")
}
