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
    level1 = level1Category?.let { level1 },
    level2 = level2Category?.let { level2 },
    quantityApplyYn = quantityApplyYn
)

fun buildSubsidiaryMaterialMappingCreateModel(
    basicProductId: Long? = null,
    subsidiaryMaterial: BasicProductCreateModel = buildBasicProductSubCreateModel(),
    seasonalOption: SeasonalOption = SeasonalOption.ALL,
    quantity: Int = 1,
) = SubsidiaryMaterialMappingCreateModel(
    basicProductId = basicProductId,
    subsidiaryMaterial = subsidiaryMaterial,
    seasonalOption = seasonalOption,
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
    subsidiaryMaterialMappingModels: MutableList<SubsidiaryMaterialMappingCreateModel> = mutableListOf(),
): BasicProductDetailCreateModel {
    return BasicProductDetailCreateModel(subsidiaryMaterialMappingModels = subsidiaryMaterialMappingModels)
        .apply { this.basicProductModel = basicProductModel }
}

fun buildBasicProductCreateModel(
    type: BasicProductType = BasicProductType.BASIC,
    partnerId: Long? = 1,
    name: String? = "테스트 기본상품",
    barcodeYn: String = "N",
    basicProductCategory: BasicProductCategoryCreateModel? = buildBasicProductCategoryCreateModel(),
    subsidiaryMaterialCategory: SubsidiaryMaterialCategoryCreateModel? = null,
    handlingTemperature: HandlingTemperatureType? = null,
    warehouse: WarehouseCreateModel = buildWarehouseCreateModel(partnerId),
    singlePackagingYn: String = "N",
    expirationDateManagementYn: String = "N",
    piecesPerBox: Int? = 2,
    boxesPerPalette: Int? = 1,
) = BasicProductCreateModel(
    type = type,
    partnerId = partnerId,
    name = name,
    barcodeYn = barcodeYn,
    basicProductCategory = basicProductCategory,
    subsidiaryMaterialCategory = subsidiaryMaterialCategory,
    handlingTemperature = handlingTemperature,
    warehouse = warehouse,
    singlePackagingYn = singlePackagingYn,
    expirationDateManagementYn = expirationDateManagementYn,
    piecesPerBox = piecesPerBox,
    boxesPerPalette = boxesPerPalette,
    activeYn = "Y"
)

fun buildBasicProductSubCreateModel(
    id: Long? = 2,
    type: BasicProductType = BasicProductType.SUB,
    partnerId: Long? = 1,
    name: String? = "종이박스",
    basicProductCategory: BasicProductCategoryCreateModel? = null,
    subsidiaryMaterialCategory: SubsidiaryMaterialCategoryCreateModel? = buildSubsidiaryMaterialCategoryCreateModel(),
    handlingTemperature: HandlingTemperatureType? = null,
    warehouse: WarehouseCreateModel = buildWarehouseCreateModel(partnerId),
) = BasicProductCreateModel(
    id = id,
    type = type,
    partnerId = partnerId,
    name = name,
    basicProductCategory = basicProductCategory,
    subsidiaryMaterialCategory = subsidiaryMaterialCategory,
    handlingTemperature = handlingTemperature,
    warehouse = warehouse,
    activeYn = "Y"
)
