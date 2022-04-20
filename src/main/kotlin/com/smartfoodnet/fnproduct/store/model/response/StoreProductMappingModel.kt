package com.smartfoodnet.fnproduct.store.model.response

import com.smartfoodnet.fnproduct.product.model.response.BasicProductModel
import com.smartfoodnet.fnproduct.store.entity.StoreProductMapping
import java.time.LocalDateTime

data class StoreProductMappingModel(
    var id: Long? = null,
    val basicProduct: BasicProductModel,
    val quantity: Int?,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
) {
    companion object {
        fun from(storeProductMapping: StoreProductMapping): StoreProductMappingModel {
            return storeProductMapping.run {
                StoreProductMappingModel(
                    id = id,
                    basicProduct = basicProduct.let { BasicProductModel.fromEntity(it) },
                    quantity = quantity,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
            }
        }
    }
}
