package com.smartfoodnet.fnproduct.order.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.querydsl.core.annotations.QueryProjection
import com.smartfoodnet.common.Constants.TIMESTAMP_FORMAT
import com.smartfoodnet.fnproduct.order.vo.DeliveryType
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import java.time.LocalDateTime

data class CollectedOrderFlatModel @QueryProjection constructor(
    val id: Long,
    val partnerId: Long,
    val uploadType: OrderUploadType,
    val status: OrderStatus,
    val orderNumber: String,
    val orderCode: String? = null,
    val bundleNumber: String,
    val storeId: Long?,
    val storeName: String?,
    val collectedProductCode: String,
    val collectedProductName: String,
    val collectedProductOptionName: String?,
    val quantity: Int,
    val storeProduct: StoreProductFlatModel,
    val basicProduct: BasicProductFlatModel,
    val deliveryType: DeliveryType,
    val shippingPrice: Double?,
    val name: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    @JsonFormat(pattern = TIMESTAMP_FORMAT)
    val collectedAt: LocalDateTime?
)

data class BasicProductFlatModel @QueryProjection constructor(
    val id: Long,
    val type: String,
    val name: String,
    val shippingProductId: Long? = null,
    val shippingProductCode: String? = null,
    val salesProductId: Long? = null,
    val salesProductCode: String? = null,
    val mappedQuantity: Int
)

data class StoreProductFlatModel @QueryProjection constructor(
    val storeProductId: Long?,
    val storeProductName: String?,
    val storeProductCode: String?,
    val storeProductOptionName: String?,
    val storeProductOptionCode: String?
)