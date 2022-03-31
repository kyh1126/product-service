package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.apiclient.dto.AddBarcode

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NosnosDeliveryAgencyInfoModel(
    val releaseItemId: Int? = null,
    val releaseId: Int? = null,
    val shippingProductId: Int? = null,
    val quantity: Int? = null,
    val releaseCode: String? = null,
    val releaseStatus: String? = null,
    val productName: String? = null,
    val upc: String? = null,
    val addBarcodeList: List<AddBarcode>? = null,
)
