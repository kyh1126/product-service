package com.smartfoodnet.apiclient.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.apiclient.dto.AddBarcode
import com.smartfoodnet.common.convertYnToInt
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import io.swagger.annotations.ApiModelProperty

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PreShippingProductModel(
    @ApiModelProperty(required = true)
    val memberId: Long,
    val productCode: String? = null,
    @ApiModelProperty(required = true)
    val productName: String,
    val supplyCompanyId: Int? = null,
    val supplierId: Int? = null,
    val categoryId: Int? = null,
    val upc: String? = null,
    val addBarcodeList: List<AddBarcode>? = null,
    val manageCode1: String? = null,
    val manageCode2: String? = null,
    val manageCode3: String? = null,
    val productDesc: String? = null,
    val singleHeight: Int? = null,
    val singleLength: Int? = null,
    val singleWeight: Int? = null,
    val singleWidth: Int? = null,
    val boxHeight: Int? = null,
    val boxLength: Int? = null,
    val boxWeight: Int? = null,
    val boxWidth: Int? = null,
    val singleEta: Int? = null,
    val paletCount: Int? = null,
    val useExpireDate: Int? = null,
    val expireDateByMakeDate: Int? = null,
    val restrictedExpireDate: Int? = null,
    val useMakeDate: Int? = null,
    val warningExpireDate: Int? = null,
    val editCode: String? = null,
    val maxQuantityPerBox: Int? = null,
    val locationId: Int? = null,
    val locationQuantity: Int? = null,
    val status: Int? = null,
    val addSalesProduct: Int? = null
) {
    companion object {
        fun fromEntity(basicProduct: BasicProduct): PreShippingProductModel {
            return basicProduct.run {
                PreShippingProductModel(
                    memberId = partnerId!!,
                    productCode = code,
                    productName = name!!,
                    manageCode2 = handlingTemperature?.desc,
                    upc = barcode,
                    singleEta = piecesPerBox,
                    paletCount = piecesPerPalette,
                    useExpireDate = convertWithExpirationDateManagementYn(
                        expirationDateManagementYn,
                        expirationDateInfo?.expirationDateWriteYn
                    ),
                    expireDateByMakeDate = convertWithExpirationDateManagementYn(
                        expirationDateManagementYn,
                        expirationDateInfo?.manufactureToExpirationDate
                    ),
                    useMakeDate = convertWithExpirationDateManagementYn(
                        expirationDateManagementYn,
                        expirationDateInfo?.manufactureDateWriteYn
                    ),
                    status = convertYnToInt(activeYn),
                    addSalesProduct = 1
                )
            }
        }

        private fun convertWithExpirationDateManagementYn(
            expirationDateManagementYn: String,
            yn: String?
        ): Int {
            if (expirationDateManagementYn == "N") {
                return 0
            }
            return convertYnToInt(yn)
        }

        private fun convertWithExpirationDateManagementYn(
            expirationDateManagementYn: String,
            yn: Int?
        ): Int {
            if (expirationDateManagementYn == "N") {
                return 0
            }
            return yn ?: 0
        }
    }
}
