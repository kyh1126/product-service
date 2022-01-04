package com.smartfoodnet.fninventory.shortage.model

/**
결품현황을 위해 orderDetail 에서 쿼리하는 모델
 */
data class ShortageOrderProjectionModel(
    val basicProductId: Long? = null,

    val basicProductName: String? = null,

    val basicProductCode: String? = null,

    val shortageOrderCount: Long? = null,

    val shippingProductId: Long? = null,

    val totalOrderCount: Int? = null,

    val totalShortagePrice: Double? = null
)