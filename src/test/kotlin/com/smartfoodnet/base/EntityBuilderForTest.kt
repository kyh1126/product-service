package com.smartfoodnet.base

import com.smartfoodnet.fnproduct.code.entity.Code

// ---------------------------------------------------------------------------------------------------------------------
// -- BasicProductCategory
// ---------------------------------------------------------------------------------------------------------------------
const val basicProductCategoryLevel1GroupId = 1
const val basicProductCategoryLevel2GroupId = 2

internal val BasicProductCategories = mapOf(
    "농산" to listOf("쌀", "잡곡/혼합곡", "채소", "과일", "견과류", "건과일", "냉동과일"),
    "수산" to listOf("건어물", "생선류", "해산물", "해조류", "젓갈"),
    "축산" to listOf("국내산 육우", "한우", "수입쇠고기", "돼지고기", "닭고기", "오리고기")
)

fun buildBasicProductCategoryLevel1(
    keyId: Int = 1,
    keyName: String = "농산",
): Code {
    return Code(
        groupId = basicProductCategoryLevel1GroupId,
        groupName = "basic_product_category.level_1_category",
        keyId = keyId,
        textKey = "level1_$keyId",
        keyName = keyName
    )
}

fun buildBasicProductCategoryLevel2(
    keyId: Int = 1,
    keyName: String = BasicProductCategories["농산"]?.get(0)!!,
) = Code(
    groupId = basicProductCategoryLevel2GroupId,
    groupName = "basic_product_category.level_2_category",
    keyId = keyId,
    textKey = "level2_$keyId",
    keyName = keyName
)
