package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.apiclient.dto.AddBarcode
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.release.entity.ReleaseProduct

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NosnosReleaseItemModel(
    val releaseItemId: Long? = null,
    val releaseId: Long? = null,
    val shippingProductId: Long? = null,
    val quantity: Int? = null,
    val releaseCode: String? = null,
    val releaseStatus: String? = null,
    val productName: String? = null,
    val upc: String? = null,
    val addBarcodeList: List<AddBarcode>? = null,
) {
    fun toEntity(basicProduct: BasicProduct): ReleaseProduct {
        return ReleaseProduct(
            releaseItemId = releaseItemId!!,
            basicProduct = basicProduct,
            quantity = quantity ?: 0
        )
    }
}
