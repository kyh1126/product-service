package com.smartfoodnet.fnproduct.order.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.querydsl.core.annotations.QueryProjection
import com.smartfoodnet.common.Constants.TIMESTAMP_FORMAT
import com.smartfoodnet.fnproduct.order.model.OrderStatus
import java.time.LocalDateTime

data class CollectedOrderModel @QueryProjection constructor(
    val id: Long,
    val partnerId: Long,
    val uploadType: String,
    val status: OrderStatus,
    val orderNumber: String,
    val bundleNumber: String,
    val basicProductId: Long?,
    val basicProductShippingProductId: Long?,
    val basicProductShippingProductCode: String?,
    val basicProductName: String?,
    @JsonIgnore
    val mappedQuantity: Int?,
    val storeId: Long?,
    val storeName: String?,
    val collectedProductCode: String,
    val collectedProductName: String,
    val collectedProductOptionName: String?,
    val storeProductId: Long?,
    val storeProductName: String?,
    val storeProductCode: String?,
    val storeProductOptionName: String?,
    val storeProductOptionCode: String?,
    val quantity: Int?,
    val deliveryType: String?,
    val shippingPrice: Double?,
    val name: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    @JsonFormat(pattern = TIMESTAMP_FORMAT)
    val collectedAt: LocalDateTime?
) {
    val mappedFlag: Boolean = basicProductId != null
    var availableQuantity: Int = -1
    val mappedQuantityCalc: Int = (mappedQuantity ?: 0) * (quantity ?: 0)
}