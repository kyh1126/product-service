package com.smartfoodnet.fnproduct.claim.model

import com.smartfoodnet.fnproduct.claim.entity.ReturnProduct
import com.smartfoodnet.fnproduct.product.model.response.BasicProductModel

data class ReturnProductModel(
    var id: Long? = null,
    var basicProduct: BasicProductModel,
    var requestQuantity: Int,
    var inboundQuantity: Int? = null,
    var discardedQuantity: Int? = null,
){
    companion object {
        fun from(returnProduct: ReturnProduct): ReturnProductModel {
            return returnProduct.run {
                ReturnProductModel(
                    id = id,
                    basicProduct = BasicProductModel.fromEntity(basicProduct),
                    requestQuantity = requestQuantity,
                    inboundQuantity = inboundQuantity,
                    discardedQuantity = discardedQuantity
                )
            }
        }
    }
}

