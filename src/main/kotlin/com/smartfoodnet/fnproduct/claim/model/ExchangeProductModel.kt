package com.smartfoodnet.fnproduct.claim.model

import com.smartfoodnet.fnproduct.claim.entity.ExchangeProduct
import com.smartfoodnet.fnproduct.order.model.ReceiverModel
import com.smartfoodnet.fnproduct.product.model.response.BasicProductModel
import java.time.LocalDateTime

data class ExchangeProductModel(
    var id: Long? = null,
    var basicProduct: BasicProductModel? = null,
    var requestQuantity: Int,
    var trackingNumber: String? = null,
    var trackingNumberRegisteredAt: LocalDateTime? = null,
    var shippingCompletedAt: LocalDateTime? = null,
    var receiver: ReceiverModel? = null,
) {

    companion object {
        fun from(exchangeProduct: ExchangeProduct): ExchangeProductModel {
            return exchangeProduct.run {
                ExchangeProductModel(
                    id = id,
                    basicProduct = BasicProductModel.fromEntity(basicProduct),
                    requestQuantity = requestQuantity,
                    trackingNumber = trackingNumber,
                    trackingNumberRegisteredAt = trackingNumberRegisteredAt,
                    shippingCompletedAt = shippingCompletedAt,
                    receiver = ReceiverModel.from(receiver)
                )
            }
        }
    }
}