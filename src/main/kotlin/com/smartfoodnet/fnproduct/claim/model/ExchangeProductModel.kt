package com.smartfoodnet.fnproduct.claim.model

import com.smartfoodnet.fnproduct.claim.entity.ExchangeProduct
import com.smartfoodnet.fnproduct.product.model.response.BasicProductModel

data class ExchangeProductModel(
    var id: Long? = null,
    var basicProduct: BasicProductModel? = null,
    var requestQuantity: Int,
) {

    companion object {
        fun from(exchangeProduct: ExchangeProduct): ExchangeProductModel {
            return exchangeProduct.run {
                ExchangeProductModel(
                    id = id,
                    basicProduct = BasicProductModel.fromEntity(basicProduct),
                    requestQuantity = requestQuantity
                )
            }
        }
    }
}