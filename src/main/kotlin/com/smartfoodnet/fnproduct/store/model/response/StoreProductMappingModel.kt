package com.smartfoodnet.fnproduct.store.model.response

import com.smartfoodnet.fnproduct.product.model.response.BasicProductModel
import com.smartfoodnet.fnproduct.store.entity.StoreProductMapping

data class StoreProductMappingModel(
    var basicProduct: BasicProductModel?,
    var quantity: Int?
) {
    companion object {
        fun from(storeProductMapping: StoreProductMapping): StoreProductMappingModel {
            return storeProductMapping.run {
                StoreProductMappingModel(
                    basicProduct = basicProduct?.let { BasicProductModel.fromEntity(it) },
                    quantity = quantity
                )
            }
        }
    }
}
