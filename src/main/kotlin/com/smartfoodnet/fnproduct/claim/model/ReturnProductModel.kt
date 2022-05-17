package com.smartfoodnet.fnproduct.claim.model

import com.smartfoodnet.fnproduct.claim.entity.ReturnProduct
import com.smartfoodnet.fnproduct.product.model.response.BasicProductSimpleModel

data class ReturnProductModel(
    val id: Long? = null,
    val basicProduct: BasicProductSimpleModel,
    val requestQuantity: Int,
    var originalReleaseQuantity: Int? = null,
    val inboundQuantity: Int? = null,
    val discardedQuantity: Int? = null,
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

