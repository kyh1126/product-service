package com.smartfoodnet.fnproduct.order.model

class RequestOrderCreateModel(
    val promotion: String? = null,
    val reShipmentReason: String? = null,
    val collectedOrderIds: List<Long>
)