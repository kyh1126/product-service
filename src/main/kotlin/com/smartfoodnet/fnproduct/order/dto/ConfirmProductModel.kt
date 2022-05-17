package com.smartfoodnet.fnproduct.order.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.querydsl.core.annotations.QueryProjection
import com.smartfoodnet.common.Constants.DATE_FORMAT
import com.smartfoodnet.common.Constants.TIMESTAMP_FORMAT
import com.smartfoodnet.fnproduct.order.vo.DeliveryType
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.order.vo.MatchingType
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import java.time.LocalDateTime

data class ConfirmProductModel @QueryProjection constructor(
    val collectedId: Long,
    val confirmProductId: Long,
    val partnerId: Long,
    val uploadType: OrderUploadType,
    val status: OrderStatus,
    val orderNumber: String,
    @JsonFormat(pattern = TIMESTAMP_FORMAT)
    val orderedAt: LocalDateTime,
    val bundleNumber: String,
    val matchingType: MatchingType,
    val basicProductId: Long?,
    val basicProductCode: String?,
    val basicProductType: BasicProductType,
    val basicProductSalesProductId: Long?,
    val basicProductSalesProductCode: String?,
    val basicProductShippingProductId: Long?,
    val basicProductShippingProductCode: String?,
    val basicProductName: String?,
    @JsonIgnore
    val confirmQuantity: Int?,
    @JsonIgnore
    val confirmQuantityPerUnit: Int?,
    val storeId: Long?,
    val storeName: String?,
    val collectedProductCode: String,
    val collectedProductName: String,
    val collectedProductOptionName: String?,
    val collectedProductOptionCode: String?,
    val storeProductId: Long?,
    val storeProductName: String?,
    val storeProductCode: String?,
    val storeProductOptionName: String?,
    val storeProductOptionCode: String?,
    val quantity: Int?,
    val deliveryType: DeliveryType,
    val shippingPrice: Double?,
    val name: String? = null,
    val zipCode: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    @JsonFormat(pattern = TIMESTAMP_FORMAT)
    val collectedAt: LocalDateTime?
) {
    val mappedFlag: Boolean = basicProductId != null
    var availableQuantity: Int = -1
    val mappedQuantityCalc: Int
        get() = when (matchingType) {
            MatchingType.TEMP -> (confirmQuantity ?: 0)
            else -> (confirmQuantityPerUnit ?: 0) * (quantity ?: 0)
        }
}