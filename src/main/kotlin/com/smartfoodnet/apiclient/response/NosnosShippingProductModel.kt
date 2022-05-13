package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.apiclient.dto.AddBarcode
import com.smartfoodnet.fnproduct.migration.mapper.NosnosModelMapperImpl
import com.smartfoodnet.fnproduct.migration.mapper.NosnosShippingProductModelMapper
import com.smartfoodnet.fnproduct.product.entity.ShippingProductArchive

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
class NosnosShippingProductModel(
    val shippingProductId: Long,
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
    val expireDateByMakeDate: Long? = null,
    val warningExpireDate: Int? = null,
    val restrictedExpireDate: Int? = null,
    val editCode: String? = null,
    val maxQuantityPerBox: Int? = null,
    val locationId: Int? = null,
    val locationQuantity: Int? = null,
    val status: Int? = null,
    val addSalesProduct: Int? = null,
    private val modelMapperImpl: NosnosModelMapperImpl = NosnosModelMapperImpl()
) : NosnosShippingProductModelMapper by modelMapperImpl {
    fun toShippingProductArchive(partnerId: Long): ShippingProductArchive {
        return ShippingProductArchive(
            partnerId = partnerId,
            shippingProductId = shippingProductId,
            productCode = productCode,
            supplyCompanyId = supplyCompanyId,
            categoryId = categoryId,
        )
    }
}
