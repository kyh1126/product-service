package com.smartfoodnet.fnproduct.store.model.response

import com.smartfoodnet.fnproduct.store.entity.StoreProduct

data class StoreProductModel(
    var id: Long? = null,
    var storeId: Long,
    var storeCode: String,
    var storeName: String,
    var partnerId: Long,
    var name: String,
    var storeProductCode: String? = null,
    var optionName: String? = null,
    var optionCode: String? = null,
    var storeProductMappings: Set<StoreProductMappingModel>? = null
) {
    companion object {
        fun from(storeProduct: StoreProduct): StoreProductModel {
            return storeProduct.run {
                StoreProductModel(
                    id = id,
                    storeId = storeId,
                    storeCode = storeCode,
                    storeName = storeName,
                    partnerId = partnerId,
                    name = name,
                    storeProductCode = storeProductCode,
                    optionName = optionName,
                    optionCode = optionCode,
                    storeProductMappings = storeProductMappings.map { StoreProductMappingModel.from(it) }.toSet()
                )
            }
        }
    }
}
