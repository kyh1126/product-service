package com.smartfoodnet.apiclient.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.fnproduct.product.entity.BasicProduct

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PreProductMappingModel(
    val salesProductId: Long,
    val mappingList: List<ProductMappingInfo>
) {
    companion object {
        fun fromEntity(basicProduct: BasicProduct): PreProductMappingModel {
            return basicProduct.run {
                PreProductMappingModel(
                    salesProductId = salesProductId!!,
                    mappingList = listOf(ProductMappingInfo(shippingProductId = shippingProductId!!))
                )
            }
        }
    }
}

class ProductMappingInfo(
    val shippingProductId: Long,
    val quantity: Int = 1
)
