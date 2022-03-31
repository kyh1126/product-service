package com.smartfoodnet.fnproduct.order.vo

enum class ShippingMethodType(
    val description: String,
    val value: Int,
) {
    PARCEL("택배", 1),
    DIRECT("직송", 2),
    DAWN("새벽배송", 3),
    SAME_DAY("당일배송", 4),
}