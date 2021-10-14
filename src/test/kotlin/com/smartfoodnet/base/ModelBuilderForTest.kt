package com.smartfoodnet.base

import com.smartfoodnet.fnproduct.code.entity.Code
import com.smartfoodnet.fnproduct.product.model.request.*
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOption

fun buildBasicProductCategoryCreateModel(
    id: Long? = 1,
    level1Category: Code? = null,
    level2Category: Code? = null,
    level1: String? = "농산",
    level2: String? = "쌀",
) = BasicProductCategoryCreateModel(
    id = id,
    level1Category = level1Category,
    level2Category = level2Category,
    level1 = level1Category?.keyName ?: level1,
    level2 = level2Category?.keyName ?: level2
)

fun buildSubsidiaryMaterialCategoryCreateModel(
    id: Long? = 1,
    level1Category: Code? = null,
    level2Category: Code? = null,
    level1: String? = "포장재",
    level2: String? = "종이박스",
    quantityApplyYn: String = "N",
) = SubsidiaryMaterialCategoryCreateModel(
    id = id,
    level1Category = level1Category,
    level2Category = level2Category,
    level1 = if (level1Category == null) null else level1,
    level2 = if (level2Category == null) null else level2,
    quantityApplyYn = quantityApplyYn
)

fun buildSubsidiaryMaterialCreateModel(
    basicProductId: Long? = null,
    subsidiaryMaterialId: Long,
    seasonalOption: SeasonalOption = SeasonalOption.ALL,
    name: String = "종이박스",
    quantity: Int = 1,
) = SubsidiaryMaterialCreateModel(
    basicProductId = basicProductId,
    subsidiaryMaterialId = subsidiaryMaterialId,
    seasonalOption = seasonalOption,
    name = name,
    quantity = quantity
)

fun buildWarehouseCreateModel(
    id: Long? = 1,
    partnerId: Long = 1,
    name: String = "입고처(주)파이",
) = WarehouseCreateModel(
    id = id,
    partnerId = partnerId,
    name = name,
)

fun buildBasicProductDetailCreateModel(
    basicProductModel: BasicProductCreateModel = buildBasicProductCreateModel(),
    subsidiaryMaterialModels: MutableList<SubsidiaryMaterialCreateModel> = mutableListOf(),
): BasicProductDetailCreateModel {
    return BasicProductDetailCreateModel(
        subsidiaryMaterialModels = subsidiaryMaterialModels,
    ).apply { this.basicProductModel = basicProductModel }
}

fun buildBasicProductCreateModel(
    type: BasicProductType = BasicProductType.BASIC,
    partnerId: Long? = 1,
    name: String? = "테스트 기본상품",
    basicProductCategory: BasicProductCategoryCreateModel? = buildBasicProductCategoryCreateModel(),
    subsidiaryMaterialCategory: SubsidiaryMaterialCategoryCreateModel? = null,
    handlingTemperature: HandlingTemperatureType? = null,
    warehouse: WarehouseCreateModel? = buildWarehouseCreateModel(partnerId),
) = BasicProductCreateModel(
    type = type,
    partnerId = partnerId,
    name = name,
    basicProductCategory = basicProductCategory,
    subsidiaryMaterialCategory = subsidiaryMaterialCategory,
    handlingTemperature = handlingTemperature,
    warehouse = warehouse,
    activeYn = "Y"
)
