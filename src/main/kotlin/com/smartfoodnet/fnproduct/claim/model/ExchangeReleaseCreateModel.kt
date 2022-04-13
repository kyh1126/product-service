package com.smartfoodnet.fnproduct.claim.model

import com.smartfoodnet.fnproduct.order.model.ReceiverModel

data class ExchangeReleaseCreateModel(
    val claimId: Long,
    val receiver: ReceiverModel? = null,
    val returnProducts: List<ExchangeProductCreateModel>
)

data class ExchangeProductCreateModel(
    val basicProductId: Long,
    val quantity: Int
)
