package com.smartfoodnet.base

import com.smartfoodnet.fnproduct.code.entity.Code
import com.smartfoodnet.fnproduct.product.entity.*
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.warehouse.entity.InWarehouse
import kotlin.random.Random

// ---------------------------------------------------------------------------------------------------------------------
// -- Code
// ---------------------------------------------------------------------------------------------------------------------
const val BPCLevel1GroupId = 1
const val BPCLevel1GroupName = "basic_product_category.level_1_category"
const val BPCLevel2GroupId = 2
const val BPCLevel2GroupName = "basic_product_category.level_2_category"
const val SMCLevel1GroupId = 3
const val SMCLevel1GroupName = "subsidiary_material_category.level_1_category"
const val SMCLevel2GroupId = 4
const val SMCLevel2GroupName = "subsidiary_material_category.level_2_category"

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
internal val SubsidiaryMaterialCategoryCodes = listOf(
    // Subsidiary Material Category > level1
    Code(7, SMCLevel1GroupId, SMCLevel1GroupName, "level1_1", 1, "포장재"),
    Code(8, SMCLevel1GroupId, SMCLevel1GroupName, "level1_2", 2, "완충재"),
    // Subsidiary Material Category > level2
    Code(9, SMCLevel2GroupId, SMCLevel2GroupName, "level2_1", 1, "종이박스"),
    Code(10, SMCLevel2GroupId, SMCLevel2GroupName, "level2_2", 2, "아이스박스"),
    Code(11, SMCLevel2GroupId, SMCLevel2GroupName, "level2_3", 3, "에어캡"),
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
    var tmpId: Long = 1
    BasicProductCategories.keys.map { level1Id ->
        val level1Category = BasicProductCategoryCodes.fromId(level1Id)
        result.addAll(
            BasicProductCategories[level1Id]!!.map {
                BasicProductCategory(
                    id = tmpId++,
                    level1Category = level1Category,
                    level2Category = BasicProductCategoryCodes.fromId(it)
                )
            }
        )
    }
    return result
}

// ---------------------------------------------------------------------------------------------------------------------
// -- SubsidiaryMaterialCategory
// ---------------------------------------------------------------------------------------------------------------------
internal val SubsidiaryMaterialCategories = mapOf(
    7L to listOf(9L, 10L),
    8L to listOf(11L)
)

fun buildSubsidiaryMaterialCategory(): List<SubsidiaryMaterialCategory> {
    val result = mutableListOf<SubsidiaryMaterialCategory>()
    var tmpId: Long = 1
    SubsidiaryMaterialCategories.keys.map { level1Id ->
        val level1Category = SubsidiaryMaterialCategoryCodes.fromId(level1Id)
        result.addAll(
            SubsidiaryMaterialCategories[level1Id]!!.map {
                SubsidiaryMaterialCategory(
                    id = tmpId++,
                    level1Category = level1Category,
                    level2Category = SubsidiaryMaterialCategoryCodes.fromId(it)
                )
            }
        )
    }
    return result
}

// ---------------------------------------------------------------------------------------------------------------------
// -- Partner
// ---------------------------------------------------------------------------------------------------------------------
const val partnerId = 1L
const val partnerCode = "0001"

// ---------------------------------------------------------------------------------------------------------------------
// -- Warehouse
// ---------------------------------------------------------------------------------------------------------------------
fun buildWarehouse(partnerId: Long) =
    InWarehouse(
        id = Random.nextLong(0, Long.MAX_VALUE),
        name = "입고처(주)파이",
        partnerId = partnerId,
        postNumber = "12345",
        address = "서울시 동대문구",
        addressDetail = "101-1",
        representative = "대표자",
        businessNumber = "1234567890",
        contactNumber = "01034561234",
        managerName = "담당자",
        managerEmail = "manager@kakao.com",
        managerContactNumber = ""
    )
//    Warehouse(id = Random.nextLong(0, Long.MAX_VALUE), name = "입고처(주)파이", partnerId = partner.id!!)

// ---------------------------------------------------------------------------------------------------------------------
// -- BasicProduct
// ---------------------------------------------------------------------------------------------------------------------
fun buildBasicProduct_SUB(
    partnerId: Long = 1,
    name: String = "테스트 공통부자재 소분류",
    subsidiaryMaterialCategory: SubsidiaryMaterialCategory,
): BasicProduct {
    return BasicProduct(
        id = Random.nextLong(0, Long.MAX_VALUE),
        type = BasicProductType.SUB,
        partnerId = partnerId,
        name = name,
        subsidiaryMaterialCategory = subsidiaryMaterialCategory,
        warehouse = buildWarehouse(partnerId),
        supplyPrice = 120,
        activeYn = "Y",
        singleDimension = SingleDimension.default,
        boxDimension = BoxDimension.default
    )
}
