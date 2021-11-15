package com.smartfoodnet.base

import com.smartfoodnet.fnproduct.code.entity.Code
import com.smartfoodnet.fnproduct.product.model.request.BasicProductCreateModel
import com.smartfoodnet.fnproduct.product.model.request.BasicProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.request.SubsidiaryMaterialCategoryCreateModel
import com.smartfoodnet.fnproduct.product.model.request.SubsidiaryMaterialMappingCreateModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOption

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
    basicProductCategoryName: String? = "농산 / 쌀",
    subsidiaryMaterialCategory: SubsidiaryMaterialCategoryCreateModel? = null,
    handlingTemperature: HandlingTemperatureType? = null,
    warehouseName: String = "입고처(주)파이",
    singlePackagingYn: String = "N",
    expirationDateManagementYn: String = "N",
    piecesPerBox: Int? = 2,
    boxesPerPalette: Int? = 1,
) = BasicProductCreateModel(
    type = type,
    partnerId = partnerId,
    name = name,
    barcodeYn = barcodeYn,
    basicProductCategoryName = basicProductCategoryName,
    subsidiaryMaterialCategory = subsidiaryMaterialCategory,
    handlingTemperature = handlingTemperature,
    warehouseName = warehouseName,
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
    basicProductCategoryName: String? = null,
    subsidiaryMaterialCategory: SubsidiaryMaterialCategoryCreateModel? = buildSubsidiaryMaterialCategoryCreateModel(),
    handlingTemperature: HandlingTemperatureType? = null,
    warehouseName: String = "입고처(주)파이",
) = BasicProductCreateModel(
    id = id,
    type = type,
    partnerId = partnerId,
    name = name,
    basicProductCategoryName = basicProductCategoryName,
    subsidiaryMaterialCategory = subsidiaryMaterialCategory,
    handlingTemperature = handlingTemperature,
    warehouseName = warehouseName,
    activeYn = "Y"
)
