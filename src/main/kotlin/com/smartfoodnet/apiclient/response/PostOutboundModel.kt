package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.vo.ShippingCodeStatus

class PostOutboundModel(
    @JsonProperty("order_code")
    val orderCode: String,
    @JsonProperty("order_id")
    val orderId: Long
){
    fun toReleaseInfo() : ReleaseInfo{
        return ReleaseInfo(
            orderId = orderId,
            orderCode = orderCode,
            shippingCodeStatus = ShippingCodeStatus.NONE
        )
    }
}
