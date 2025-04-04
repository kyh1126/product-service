package com.smartfoodnet.apiclient.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.apiclient.dto.AddBarcode
import com.smartfoodnet.common.convertYnToInt
import com.smartfoodnet.common.convertYnToLong
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
    val singleWidth: Int? = null,
    val singleLength: Int? = null,
    val singleHeight: Int? = null,
    val singleWeight: Int? = null,
    val boxWidth: Int? = null,
    val boxLength: Int? = null,
    val boxHeight: Int? = null,
    val boxWeight: Int? = null,
    val singleEta: Int? = null,
    val paletCount: Int? = null,
    val useExpireDate: Long? = null,
    val expireDateByMakeDate: Long? = null,
    val restrictedExpireDate: Int? = null,
    val useMakeDate: Long? = null,
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
                    productName = name,
                    upc = barcode,
                    manageCode3 = handlingTemperature?.desc,
                    singleWidth = singleDimension.width,
                    singleLength = singleDimension.length,
                    singleHeight = singleDimension.height,
                    singleWeight = singleDimension.weight,
                    boxWidth = boxDimension.width,
                    boxLength = boxDimension.length,
                    boxHeight = boxDimension.height,
                    boxWeight = boxDimension.weight,
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
        ): Long {
            if (expirationDateManagementYn == "N") {
                return 0L
            }
            return convertYnToLong(yn)
        }

        private fun convertWithExpirationDateManagementYn(
            expirationDateManagementYn: String,
            yn: Long?
        ): Long {
            if (expirationDateManagementYn == "N") {
                return 0L
            }
            return yn ?: 0L
        }
    }
}
