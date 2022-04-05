package com.smartfoodnet.fnproduct.order.model

import com.smartfoodnet.fnproduct.order.vo.DeliveryType

class RequestOrderCreateModel(
    val promotion: String? = null,
    val reShipmentReason: String? = null,
    val collectedOrderIds: List<Long>,
    val deliveryType: DeliveryType? = DeliveryType.PARCEL
)