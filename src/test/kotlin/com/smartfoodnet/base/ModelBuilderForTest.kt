package com.smartfoodnet.base

import com.smartfoodnet.fnproduct.code.entity.Code
import com.smartfoodnet.fnproduct.product.model.request.*
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOption

fun buildSubsidiaryMaterialCategoryCreateModel(
    id: Long? = 1,
    level1Category: Code? = null,
    level2Category: Code? = null,
    level1: String? = "보냉재",
    level2: String? = "드라이아이스",
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
    name: String = "드라이아이스",
    quantity: Int = 1,
) = SubsidiaryMaterialCreateModel(
    basicProductId = basicProductId,
    subsidiaryMaterialId = subsidiaryMaterialId,
    seasonalOption = seasonalOption,
    name = name,
    quantity = quantity
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
    basicProductCategory: BasicProductCategoryCreateModel? = null,
    subsidiaryMaterialCategory: SubsidiaryMaterialCategoryCreateModel? = null,
    handlingTemperature: HandlingTemperatureType? = null,
    warehouse: WarehouseCreateModel? = null,
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
