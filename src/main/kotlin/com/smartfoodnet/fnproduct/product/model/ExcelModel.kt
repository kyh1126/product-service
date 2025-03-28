package com.smartfoodnet.fnproduct.product.model

import com.smartfoodnet.apiclient.response.PartnerIdPairModel
import com.smartfoodnet.fnproduct.product.model.request.BasicProductCreateModel
import com.smartfoodnet.fnproduct.product.model.request.BasicProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.request.ExpirationDateInfoCreateModel
import com.smartfoodnet.fnproduct.product.model.request.SubsidiaryMaterialMappingCreateModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOption

interface ExcelModel

data class ExpirationDateInfoExcelModel(
    val manufactureDateWriteYn: String,
    val expirationDateWriteYn: String,
    var manufactureToExpirationDate: Long? = 0,
) {
    fun toExpirationDateInfoCreateModel(): ExpirationDateInfoCreateModel {
        return ExpirationDateInfoCreateModel(
            manufactureDateWriteYn = manufactureDateWriteYn,
            expirationDateWriteYn = expirationDateWriteYn,
            manufactureToExpirationDate = manufactureToExpirationDate
        )
    }

    fun isExpirationDateManagement(manufactureDateWriteYn: String, expirationDateWriteYn: String) =
        !((manufactureDateWriteYn.isEmpty() || expirationDateWriteYn.isEmpty())
            || (manufactureDateWriteYn == "N" && expirationDateWriteYn == "N"))
}

data class BasicProductExcelModel(
    val rowIdx: Int,
    val memberId: Long,
    val shippingProductId: Long,
    val productName: String,
    val barcode: String?,
    val piecesPerBox: Int,
    val piecesPerPalette: Int?,
    val expirationDateInfoExcelModel: ExpirationDateInfoExcelModel,
    val activeYn: String,
) : ExcelModel {
    fun toBasicProductDetailCreateModel(
        defaultBasicProductSubId: Long,
        partnerModel: PartnerIdPairModel
    ): BasicProductDetailCreateModel {
        val subsidiaryMaterialMappingCreateModel = SubsidiaryMaterialMappingCreateModel(
            subsidiaryMaterialId = defaultBasicProductSubId,
            seasonalOption = SeasonalOption.ALL,
            quantity = 1
        )

        val basicProductModel = BasicProductCreateModel().also {
            it.type = BasicProductType.BASIC
            it.partnerId = partnerModel.partnerId
            it.partnerCode = partnerModel.partnerCode
            it.shippingProductId = shippingProductId
            it.name = productName
            it.barcodeYn = if (barcode.isNullOrEmpty()) "N" else "Y"
            it.barcode = barcode
            it.piecesPerBox = piecesPerBox
            it.piecesPerPalette = piecesPerPalette
            it.expirationDateManagementYn = isExpirationDateManagement(expirationDateInfoExcelModel)
            it.expirationDateInfoModel =
                expirationDateInfoExcelModel.toExpirationDateInfoCreateModel()
            it.activeYn = activeYn
        }

        return BasicProductDetailCreateModel(
            subsidiaryMaterialMappingModels = mutableListOf(subsidiaryMaterialMappingCreateModel)
        ).also {
            it.basicProductModel = basicProductModel
        }
    }

    private fun isExpirationDateManagement(expirationDateInfo: ExpirationDateInfoExcelModel): String {
        val (manufactureDateWriteYn, expirationDateWriteYn, _) = expirationDateInfo
        if (manufactureDateWriteYn.isEmpty() || expirationDateWriteYn.isEmpty()) {
            return "N"
        }
        return "Y"
    }
}
