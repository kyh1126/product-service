package com.smartfoodnet.base

import com.smartfoodnet.fnproduct.product.model.request.*
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOption

fun buildSubsidiaryMaterialMappingCreateModel(
    subsidiaryMaterialId: Long = buildBasicProductSubCreateModel().id!!,
    seasonalOption: SeasonalOption = SeasonalOption.ALL,
    quantity: Int = 1,
) = SubsidiaryMaterialMappingCreateModel(
    subsidiaryMaterialId = subsidiaryMaterialId,
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
    partnerCode: String? = "0001",
    name: String = "테스트 기본상품",
    barcodeYn: String = "N",
    basicProductCategoryId: Long? = 1,
    subsidiaryMaterialCategoryId: Long? = 1,
    handlingTemperature: HandlingTemperatureType? = null,
    warehouseId: Long = 1,
    expirationDateManagementYn: String = "N",
    piecesPerBox: Int? = 2,
    piecesPerPalette: Int? = 1,
) = TestBasicProductCreateModel(
    type = type,
    partnerId = partnerId,
    partnerCode = partnerCode,
    barcodeYn = barcodeYn,
    basicProductCategoryId = basicProductCategoryId,
    subsidiaryMaterialCategoryId = subsidiaryMaterialCategoryId,
    handlingTemperature = handlingTemperature,
    warehouseId = warehouseId,
    expirationDateManagementYn = expirationDateManagementYn,
    singleDimensionCreateModel = buildSingleDimensionCreateModel(),
    boxDimensionCreateModel = buildBoxDimensionCreateModel(),
    piecesPerBox = piecesPerBox,
    piecesPerPalette = piecesPerPalette,
    activeYn = "Y"
).also {
    it.name = name
}.toModel()

fun buildBasicProductSubCreateModel(
    id: Long? = 2,
    type: BasicProductType = BasicProductType.SUB,
    partnerId: Long? = 1,
    name: String = "종이박스",
    basicProductCategoryId: Long? = null,
    subsidiaryMaterialCategoryId: Long? = 1,
    handlingTemperature: HandlingTemperatureType? = null,
    warehouseId: Long = 1,
) = TestBasicProductCreateModel(
    id = id,
    type = type,
    partnerId = partnerId,
    basicProductCategoryId = basicProductCategoryId,
    subsidiaryMaterialCategoryId = subsidiaryMaterialCategoryId,
    handlingTemperature = handlingTemperature,
    warehouseId = warehouseId,
    singleDimensionCreateModel = buildSingleDimensionCreateModel(),
    boxDimensionCreateModel = buildBoxDimensionCreateModel(),
    activeYn = "Y"
).also {
    it.name = name
}.toModel()

fun buildSingleDimensionCreateModel() = SingleDimensionCreateModel().also {
    it.singleWidth = 0
    it.singleLength = 0
    it.singleHeight = 0
    it.singleWeight = null
}

fun buildBoxDimensionCreateModel() = BoxDimensionCreateModel().also {
    it.boxWidth = 0
    it.boxLength = 0
    it.boxHeight = 0
    it.boxWeight = null
}
