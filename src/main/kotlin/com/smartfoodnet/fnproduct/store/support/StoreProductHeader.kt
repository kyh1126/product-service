package com.smartfoodnet.fnproduct.store.support

enum class StoreProductHeader(val headerTitle: String) {
    STORE_ID("쇼핑몰 ID"),
    STORE_NAME("쇼핑몰이름"),
    STORE_CODE("쇼핑몰코드"),
    STORE_PRODUCT_CODE("쇼핑몰상품코드"),
    NAME("쇼핑몰상품명"),
    OPTION_CODE("쇼핑몰옵션코드"),
    OPTION_NAME("쇼핑몰옵션이름"),
    BASIC_PRODUCT_CODE("매칭상품코드"),
    BASIC_PRODUCT_NAME("매칭상품코드"),
    BASIC_PRODUCT_QUANTITY("기본상품 개수");

    companion object {
        fun from(text: String?): StoreProductHeader? {
            return values().find { it.headerTitle == text }
        }
    }
}

object StoreProductHeaderIndexMap {
    fun from(headers: List<String>): Map<StoreProductHeader, Int> {
        val map = mutableMapOf<StoreProductHeader, Int>()
        headers.forEachIndexed { i, value ->
            StoreProductHeader.from(value)?.also { map[it] = i }
        }
        return map
    }
}
