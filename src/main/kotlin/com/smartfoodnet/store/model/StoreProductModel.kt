package com.smartfoodnet.store.model

import com.smartfoodnet.store.entity.StoreProduct
import java.time.Instant

data class StoreProductModel(
    var id: Long? = null,
    var partnerId: Long? = null,
    var name: String? = null,
    var storeProductCode: String? = null,
    var optionName: String? = null,
    var optionCode: String? = null,
    var basicProductId: Long? = null,
    var deletedAt: Instant? = null,
    var createdAt: Instant? = null,
    var updatedAt: Instant? = null,
) {
    fun toEntity(): StoreProduct {
        return StoreProduct(
            id = id,
            partnerId = partnerId,
            name = name,
            storeProductCode = storeProductCode,
            optionName = optionName,
            optionCode = optionCode,
            deletedAt = deletedAt,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun from(storeProduct: StoreProduct): StoreProductModel {
            return storeProduct.run {
                StoreProductModel(
                    id = id,
                    partnerId = partnerId,
                    name = name,
                    storeProductCode = storeProductCode,
                    optionName = optionName,
                    optionCode = optionCode,
                    basicProductId = basicProduct?.id,
                    deletedAt = deletedAt,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
            }
        }
    }
}