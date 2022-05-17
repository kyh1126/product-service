package com.smartfoodnet.fnproduct.migration.mapper

import com.smartfoodnet.apiclient.response.NosnosShippingProductModel
import com.smartfoodnet.apiclient.response.PartnerIdPairModel
import com.smartfoodnet.fnproduct.product.model.ExpirationDateInfoExcelModel
import com.smartfoodnet.fnproduct.product.model.request.*
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOption

interface NosnosShippingProductModelMapper {
    fun toBasicProductDetailCreateModel(
        nosnosShippingProductModel: NosnosShippingProductModel,
        defaultBasicProductSubId: Long,
        partnerModel: PartnerIdPairModel,
    ): BasicProductDetailCreateModel

    fun convertToYN(target: Int?): String =
        when (target) {
            1 -> "Y"
            0, null -> "N"
            else -> throw IllegalArgumentException("YN 형식이 아닌 값이 입력되었습니다. target: ${target}")
        }

    fun isExpirationDateManagement(expirationDateInfo: ExpirationDateInfoExcelModel): String {
        val (manufactureDateWriteYn, expirationDateWriteYn, _) = expirationDateInfo
        if (!expirationDateInfo.isExpirationDateManagement(manufactureDateWriteYn, expirationDateWriteYn)) {
            return "N"
        }
        return "Y"
    }
}

class NosnosModelMapperImpl : NosnosShippingProductModelMapper {
    override fun toBasicProductDetailCreateModel(
        nosnosShippingProductModel: NosnosShippingProductModel,
        defaultBasicProductSubId: Long,
        partnerModel: PartnerIdPairModel,
    ): BasicProductDetailCreateModel {
        val subsidiaryMaterialMappingCreateModel = SubsidiaryMaterialMappingCreateModel(
            subsidiaryMaterialId = defaultBasicProductSubId,
            seasonalOption = SeasonalOption.ALL,
            quantity = 1
        )
        val basicProductModel = BasicProductCreateModel()
        with(nosnosShippingProductModel) {
            basicProductModel.also {
                val expirationDateInfoExcelModel = ExpirationDateInfoExcelModel(
                    manufactureDateWriteYn = convertToYN(useMakeDate),
                    expirationDateWriteYn = convertToYN(useExpireDate),
                    manufactureToExpirationDate = expireDateByMakeDate
                )

                it.type = BasicProductType.BASIC
                it.partnerId = partnerModel.partnerId
                it.partnerCode = partnerModel.partnerCode
                it.shippingProductId = shippingProductId
                it.name = productName
                it.barcodeYn = if (upc.isNullOrEmpty()) "N" else "Y"
                it.barcode = upc
                it.handlingTemperature = HandlingTemperatureType.fromDesc(manageCode3)
                it.expirationDateManagementYn = isExpirationDateManagement(expirationDateInfoExcelModel)
                it.expirationDateInfoModel = expirationDateInfoExcelModel.toExpirationDateInfoCreateModel()
                it.singleDimensionCreateModel = SingleDimensionCreateModel.fromModel(this)
                it.boxDimensionCreateModel = BoxDimensionCreateModel.fromModel(this)
                it.piecesPerBox = singleEta
                it.piecesPerPalette = paletCount
                it.activeYn = convertToYN(status)
            }
        }
        return BasicProductDetailCreateModel(
            subsidiaryMaterialMappingModels = mutableListOf(subsidiaryMaterialMappingCreateModel)
        ).also {
            it.basicProductModel = basicProductModel
        }
    }
}

class NosnosModelMapperTestImpl : NosnosShippingProductModelMapper {
    override fun toBasicProductDetailCreateModel(
        nosnosShippingProductModel: NosnosShippingProductModel,
        defaultBasicProductSubId: Long,
        partnerModel: PartnerIdPairModel,
    ): BasicProductDetailCreateModel {
        val subsidiaryMaterialMappingCreateModel = SubsidiaryMaterialMappingCreateModel(
            subsidiaryMaterialId = defaultBasicProductSubId,
            seasonalOption = SeasonalOption.ALL,
            quantity = 1
        )
        val basicProductModel = BasicProductCreateModel()
        with(nosnosShippingProductModel) {
            basicProductModel.also {
                val expirationDateInfoExcelModel = ExpirationDateInfoExcelModel(
                    manufactureDateWriteYn = convertToYN(useMakeDate),
                    expirationDateWriteYn = convertToYN(useExpireDate)
                ).also {
                    if (it.expirationDateWriteYn == "N" && it.manufactureDateWriteYn == "Y")
                        it.manufactureToExpirationDate = expireDateByMakeDate ?: 100 // test default
                }

                it.type = BasicProductType.BASIC
                it.partnerId = partnerModel.partnerId
                it.partnerCode = partnerModel.partnerCode
                it.shippingProductId = shippingProductId
                it.name = productName
                it.barcodeYn = if (upc.isNullOrEmpty()) "N" else "Y"
                it.barcode = upc
                it.handlingTemperature = HandlingTemperatureType.fromDesc(manageCode3)
                    ?: HandlingTemperatureType.ROOM // test default
                it.expirationDateManagementYn = isExpirationDateManagement(expirationDateInfoExcelModel)
                it.expirationDateInfoModel = expirationDateInfoExcelModel.toExpirationDateInfoCreateModel()
                it.singleDimensionCreateModel = SingleDimensionCreateModel().also { // test default
                    it.singleWidth = singleWidth ?: 10
                    it.singleLength = singleLength ?: 10
                    it.singleHeight = singleHeight ?: 10
                    it.singleWeight = singleWeight
                }
                it.boxDimensionCreateModel = BoxDimensionCreateModel().also { // test default
                    it.boxWidth = boxWidth ?: 100
                    it.boxLength = boxLength ?: 100
                    it.boxHeight = boxHeight ?: 100
                    it.boxWeight = boxWeight
                }
                it.piecesPerBox = singleEta ?: 5 // test default
                it.piecesPerPalette = paletCount
                it.activeYn = convertToYN(status)
            }
        }

        return BasicProductDetailCreateModel(
            subsidiaryMaterialMappingModels = mutableListOf(subsidiaryMaterialMappingCreateModel)
        ).also {
            it.basicProductModel = basicProductModel
        }
    }
}
