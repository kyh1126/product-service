package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.apiclient.dto.AddBarcode
import com.smartfoodnet.fnproduct.product.model.ExpirationDateInfoExcelModel
import com.smartfoodnet.fnproduct.product.model.request.BasicProductCreateModel
import com.smartfoodnet.fnproduct.product.model.request.BasicProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.request.SubsidiaryMaterialMappingCreateModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOption

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
class GetShippingProductModel(
        val shipping_product_id: Long,
        val productCode: String,
        val supplyCompanyId: Int? = null,
        val supplierId: Int? = null,
        val categoryId: Int? = null,
        val productName: String,
        val upc: String? = null,
        val addBarcodeList: List<AddBarcode>? = null,
        val manageCode1: String? = null,
        val manageCode2: String? = null,
        val manageCode3: String? = null,
        val productDesc: String? = null,
        val singleWeight: Int? = null,
        val singleLength: Int? = null,
        val singleHeight: Int? = null,
        val singleWidth: Int? = null,
        val boxWeight: Int? = null,
        val boxLength: Int? = null,
        val boxHeight: Int? = null,
        val boxWidth: Int? = null,
        val singleEta: Int? = null,
        val paletCount: Int? = null,
        val useExpireDate: Int? = null,
        val useMakeDate: Int? = null,
        val expireDateByMakeDate: Int? = null,
        val warningExpireDate: Int? = null,
        val restrictedExpireDate: Int? = null,
        val editCode: String? = null,
        val maxQuantityPerBox: Int? = null,
        val locationId: Int? = null,
        val locationQuantity: Int? = null,
        val status: Int? = null,
        val addSalesProduct: Int? = null
) {
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
            val expirationDateInfoExcelModel = ExpirationDateInfoExcelModel(
                    manufactureDateWriteYn = convertToYN(useMakeDate),
                    expirationDateWriteYn = convertToYN(useExpireDate),
                    manufactureToExpirationDate = expireDateByMakeDate
            )

            it.type = BasicProductType.BASIC
            it.partnerId = partnerModel.partnerId
            it.partnerCode = partnerModel.partnerCode
            it.shippingProductId = shipping_product_id
            it.name = productName
            it.barcodeYn = if (upc.isNullOrEmpty()) "N" else "Y"
            it.barcode = upc
            it.piecesPerBox = singleEta
            it.piecesPerPalette = paletCount
            it.expirationDateManagementYn = isExpirationDateManagement(expirationDateInfoExcelModel)
            it.expirationDateInfoModel = expirationDateInfoExcelModel.toExpirationDateInfoCreateModel()
            it.activeYn = convertToYN(status)
        }

        return BasicProductDetailCreateModel(
                subsidiaryMaterialMappingModels = mutableListOf(subsidiaryMaterialMappingCreateModel)
        ).also {
            it.basicProductModel = basicProductModel
        }
    }

    private fun convertToYN(target: Int?): String =
            when (target) {
                1 -> "Y"
                0, null -> "N"
                else -> throw IllegalArgumentException("YN 형식이 아닌 값이 입력되었습니다. target: ${target}")
            }

    private fun isExpirationDateManagement(expirationDateInfo: ExpirationDateInfoExcelModel): String {
        val (manufactureDateWriteYn, expirationDateWriteYn, _) = expirationDateInfo
        if (manufactureDateWriteYn.isEmpty() || expirationDateWriteYn.isEmpty()) {
            return "N"
        }
        return "Y"
    }
}
