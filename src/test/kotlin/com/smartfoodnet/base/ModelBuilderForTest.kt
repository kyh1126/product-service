package com.smartfoodnet.base

import com.smartfoodnet.fnproduct.product.model.request.BasicProductCreateModel
import com.smartfoodnet.fnproduct.product.model.request.BasicProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.request.SubsidiaryMaterialMappingCreateModel
import com.smartfoodnet.fnproduct.product.model.request.TestBasicProductCreateModel
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
    name: String? = "테스트 기본상품",
    barcodeYn: String = "N",
    basicProductCategoryId: Long? = 1,
    subsidiaryMaterialCategoryId: Long? = 1,
    handlingTemperature: HandlingTemperatureType? = null,
    warehouseId: Long = 1,
    singlePackagingYn: String = "N",
    expirationDateManagementYn: String = "N",
    piecesPerBox: Int? = 2,
    piecesPerPalette: Int? = 1,
) = TestBasicProductCreateModel(
    type = type,
    partnerId = partnerId,
    partnerCode = partnerCode,
    name = name,
    barcodeYn = barcodeYn,
    basicProductCategoryId = basicProductCategoryId,
    subsidiaryMaterialCategoryId = subsidiaryMaterialCategoryId,
    handlingTemperature = handlingTemperature,
    warehouseId = warehouseId,
    singlePackagingYn = singlePackagingYn,
    expirationDateManagementYn = expirationDateManagementYn,
    piecesPerBox = piecesPerBox,
    piecesPerPalette = piecesPerPalette,
    activeYn = "Y"
).toModel()

fun buildBasicProductSubCreateModel(
    id: Long? = 2,
    type: BasicProductType = BasicProductType.SUB,
    partnerId: Long? = 1,
    name: String? = "종이박스",
    basicProductCategoryId: Long? = null,
    subsidiaryMaterialCategoryId: Long? = 1,
    handlingTemperature: HandlingTemperatureType? = null,
    warehouseId: Long = 1,
) = TestBasicProductCreateModel(
    id = id,
    type = type,
    partnerId = partnerId,
    name = name,
    basicProductCategoryId = basicProductCategoryId,
    subsidiaryMaterialCategoryId = subsidiaryMaterialCategoryId,
    handlingTemperature = handlingTemperature,
    warehouseId = warehouseId,
    activeYn = "Y"
).toModel()
