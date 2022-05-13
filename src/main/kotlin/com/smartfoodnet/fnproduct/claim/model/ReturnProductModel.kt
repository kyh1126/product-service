package com.smartfoodnet.fnproduct.claim.model

import com.smartfoodnet.fnproduct.claim.entity.ReturnProduct
import com.smartfoodnet.fnproduct.product.model.response.BasicProductModel
import com.smartfoodnet.fnproduct.product.model.response.BasicProductSimpleModel

data class ReturnProductModel(
    var id: Long? = null,
    var basicProduct: BasicProductSimpleModel,
    var requestQuantity: Int,
    var inboundQuantity: Int? = null,
    var discardedQuantity: Int? = null,
){
    companion object {
        fun from(returnProduct: ReturnProduct): ReturnProductModel {
            return returnProduct.run {
                ReturnProductModel(
                    id = id,
                    basicProduct = BasicProductSimpleModel.fromEntity(basicProduct),
                    requestQuantity = requestQuantity,
                    inboundQuantity = inboundQuantity,
                    discardedQuantity = discardedQuantity
                )
            }
        }
    }
}

