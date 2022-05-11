package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.vo.TrackingNumberStatus

class PostOutboundModel(
    @JsonProperty("order_code")
    val orderCode: String,
    @JsonProperty("order_id")
    val orderId: Long
) {
    fun toReleaseInfo(partnerId: Long, confirmOrder: ConfirmOrder): ReleaseInfo {
        return ReleaseInfo(
            partnerId = partnerId,
            orderId = orderId,
            orderCode = orderCode,
            trackingNumberStatus = TrackingNumberStatus.NONE,
            confirmOrder = confirmOrder
        )
    }
}
