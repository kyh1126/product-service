package com.smartfoodnet.fnproduct.claim.model

import com.smartfoodnet.fnproduct.claim.entity.ExchangeRelease
import com.smartfoodnet.fnproduct.order.model.ReceiverModel
import java.time.LocalDateTime

data class ExchangeReleaseModel(
    val id: Long? = null,
    var receiver: ReceiverModel,
    val trackingNumber: String? = null,
    val trackingNumberRegisteredAt: LocalDateTime? = null,
    val shippingCompletedAt: LocalDateTime? = null,
    val exchangeProducts: List<ExchangeProductModel>,
    val nosnosOrderId: Long? = null,
    val nosnosOrderCode: String? = null
) {
    companion object {
        fun from(exchangeRelease: ExchangeRelease): ExchangeReleaseModel {
            return exchangeRelease.run {
                ExchangeReleaseModel(
                    id = id,
                    receiver = ReceiverModel.from(receiver),
                    trackingNumber = trackingNumber,
                    trackingNumberRegisteredAt = trackingNumberRegisteredAt,
                    shippingCompletedAt = shippingCompletedAt,
                    exchangeProducts = exchangeProducts.map { ExchangeProductModel.from(it) },
                    nosnosOrderId = nosnosOrderId,
                    nosnosOrderCode = nosnosOrderCode
                )
            }
        }
    }
}