package com.smartfoodnet.base

import com.smartfoodnet.fnproduct.code.entity.Code
import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory

// ---------------------------------------------------------------------------------------------------------------------
// -- Code
// ---------------------------------------------------------------------------------------------------------------------
const val BPCLevel1GroupId = 1
const val BPCLevel1GroupName = "basic_product_category.level_1_category"
const val BPCLevel2GroupId = 2
const val BPCLevel2GroupName = "basic_product_category.level_2_category"

internal val BasicProductCategoryCodes = listOf(
    // Basic Product Category > level1
    Code(1, BPCLevel1GroupId, BPCLevel1GroupName, "level1_1", 1, "농산"),
    Code(2, BPCLevel1GroupId, BPCLevel1GroupName, "level1_2", 2, "수산"),
    // Basic Product Category > level2
    Code(3, BPCLevel2GroupId, BPCLevel2GroupName, "level2_1", 1, "쌀"),
    Code(4, BPCLevel2GroupId, BPCLevel2GroupName, "level2_2", 2, "잡곡/혼합곡"),
    Code(5, BPCLevel2GroupId, BPCLevel2GroupName, "level2_3", 3, "채소"),
    Code(6, BPCLevel2GroupId, BPCLevel2GroupName, "level2_4", 4, "건어물"),
)

fun Collection<Code>.fromId(id: Long) = this.first { it.id == id }

// ---------------------------------------------------------------------------------------------------------------------
// -- BasicProductCategory
// ---------------------------------------------------------------------------------------------------------------------
internal val BasicProductCategories = mapOf(
    1L to listOf(3L, 4L, 5L),
    2L to listOf(6L)
)

fun buildBasicProductCategory(): List<BasicProductCategory> {
    val result = mutableListOf<BasicProductCategory>()
    BasicProductCategories.keys.map { level1Id ->
        val level1Category = BasicProductCategoryCodes.fromId(level1Id)
        result.addAll(
            BasicProductCategories[level1Id]!!.map {
                BasicProductCategory(
                    level1Category = level1Category,
                    level2Category = BasicProductCategoryCodes.fromId(it)
                )
            }
        )
    }
    return result
}
