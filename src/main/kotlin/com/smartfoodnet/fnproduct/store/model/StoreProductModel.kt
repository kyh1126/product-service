package com.smartfoodnet.fnproduct.store.model

import com.smartfoodnet.fnproduct.store.entity.StoreProduct

data class StoreProductModel(
    var id: Long? = null,
    var storeCode: String,
    var storeName: String,
    var partnerId: Long,
    var name: String,
    var storeProductCode: String? = null,
    var optionName: String? = null,
    var optionCode: String? = null,
    var basicProductId: Long? = null,
    var basicProductCode: String? = null,
    var basicProductName: String? = null,
) {
    fun toEntity(): StoreProduct {
        return StoreProduct(
            id = id,
            storeCode = storeCode,
            storeName = storeName,
            partnerId = partnerId,
            name = name,
            storeProductCode = storeProductCode,
            optionName = optionName,
            optionCode = optionCode,
        )
    }

    companion object {
        fun from(storeProduct: StoreProduct): StoreProductModel {
            return storeProduct.run {
                StoreProductModel(
                    id = id,
                    storeCode = storeCode,
                    storeName = storeName,
                    partnerId = partnerId,
                    name = name,
                    storeProductCode = storeProductCode,
                    optionName = optionName,
                    optionCode = optionCode,
                    basicProductId = basicProduct?.id,
                )
            }
        }
    }
}
