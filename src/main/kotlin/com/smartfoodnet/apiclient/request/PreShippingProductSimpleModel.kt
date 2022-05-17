package com.smartfoodnet.apiclient.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.fnproduct.product.entity.BasicProduct

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PreShippingProductSimpleModel(
    val shippingProductId: Long,
    val productName: String,
    val productCode: String,
    val supplyCompanyId: Int? = null,
    val categoryId: Int? = null,
    val upc: String? = null,
) {
    companion object {
        fun fromEntity(basicProduct: BasicProduct): PreShippingProductSimpleModel {
            return basicProduct.run {
                PreShippingProductSimpleModel(
                    shippingProductId = shippingProductId!!,
                    productCode = code!!,
                    productName = name,
                    upc = barcode,
                )
            }
        }
    }
}
