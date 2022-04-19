package com.smartfoodnet.fnproduct.store.model.response

import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import java.time.LocalDateTime

data class StoreProductModel(
    var id: Long? = null,
    var storeId: Long,
    var storeName: String,
    var storeIcon: String? = null,
    var partnerId: Long,
    var name: String,
    var storeProductCode: String? = null,
    var optionName: String? = null,
    var optionCode: String? = null,
    var storeProductMappings: Set<StoreProductMappingModel>? = null,
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null
) {
    companion object {
        fun from(storeProduct: StoreProduct): StoreProductModel {
            return storeProduct.run {
                StoreProductModel(
                    id = id,
                    storeId = storeId,
                    storeName = storeName,
                    storeIcon = storeIcon,
                    partnerId = partnerId,
                    name = name,
                    storeProductCode = storeProductCode,
                    optionName = optionName,
                    optionCode = optionCode,
                    storeProductMappings = storeProductMappings.map { StoreProductMappingModel.from(it) }.toSet(),
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
            }
        }
    }
}
