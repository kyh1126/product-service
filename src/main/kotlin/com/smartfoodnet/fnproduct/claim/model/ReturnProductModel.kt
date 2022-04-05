package com.smartfoodnet.fnproduct.claim.model

import com.smartfoodnet.fnproduct.claim.entity.ReturnProduct
import com.smartfoodnet.fnproduct.order.model.ReceiverModel
import com.smartfoodnet.fnproduct.product.model.response.BasicProductModel
import java.time.LocalDateTime

data class ReturnProductModel(
    var id: Long? = null,
    var basicProduct: BasicProductModel,
    var requestQuantity: Int,
    var releaseItemId: Long? = null,
    var trackingNumber: String? = null,
    var shippingCompletedAt: LocalDateTime? = null,
    var inboundQuantity: Int? = null,
    var discardedQuantity: Int? = null,
    var receiver: ReceiverModel? = null
){
    companion object {
        fun from(returnProduct: ReturnProduct): ReturnProductModel {
            return returnProduct.run {
                ReturnProductModel(
                    id = id,
                    basicProduct = BasicProductModel.fromEntity(basicProduct),
                    requestQuantity = requestQuantity,
                    releaseItemId = releaseItemId,
                    trackingNumber = trackingNumber,
                    shippingCompletedAt = shippingCompletedAt,
                    inboundQuantity = inboundQuantity,
                    discardedQuantity = discardedQuantity,
                    receiver = ReceiverModel.from(receiver)
                )
            }
        }
    }
}

