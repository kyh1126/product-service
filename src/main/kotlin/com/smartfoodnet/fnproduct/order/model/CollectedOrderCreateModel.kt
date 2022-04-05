package com.smartfoodnet.fnproduct.order.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fnproduct.order.entity.CollectedProductInfo
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.vo.DeliveryType
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.order.vo.StoreSyncStatus
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class CollectedOrderCreateModel(
    @ApiModelProperty(value = "화주사 ID")
    val partnerId: Long,

    @ApiModelProperty(value = "중복처리를 위한 쇼핑몰 종속적 유니크 키")
    val orderUniqueKey: String,

    @ApiModelProperty(value = "쇼핑몰 이름")
    val storeName: String,

    @ApiModelProperty(value = "쇼핑몰 코유 코드")
    val storeCode: String,

    @ApiModelProperty(value = "쇼핑몰 ID")
    val storeId: Long,

    @ApiModelProperty(value = "화주사 쇼핑몰 ID")
    val userStoreId: String? = null,

    @ApiModelProperty(value = "주문일자")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val orderedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "주문수집일")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val collectedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "주문번호")
    val orderNumber: String,

    @ApiModelProperty(value = "주문 상태")
    val status: OrderStatus,

    @ApiModelProperty(value = "클레임 상태")
    val claimStatus: String? = null,

    @ApiModelProperty(value = "쇼핑몰 상품명")
    val storeProductName: String,

    @ApiModelProperty(value = "쇼핑몰별 상품코드")
    val storeProductCode: String,

    @ApiModelProperty(value = "상품 옵션명")
    val storeProductOptionName: String? = null,

    @ApiModelProperty(value = "상태변경일자")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val statusUpdatedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "배송방식")
    val deliveryType: DeliveryType = DeliveryType.PARCEL,

    @ApiModelProperty(value = "배송완료요청일")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val expectedDeliveryDate: LocalDateTime? = null,

    @ApiModelProperty(value = "주문금액")
    val price: Double? = null,

    @ApiModelProperty(value = "배송비")
    val shippingPrice: Double? = null,

    @ApiModelProperty(value = "수량")
    val count: Int,

    val receiver: ReceiverModel,

    @ApiModelProperty(value = "업로드방식")
    val uploadType: OrderUploadType,

    @ApiModelProperty(value = "묶음번호")
    val bundleNumber: String
) {
    fun toCollectEntity(): CollectedOrder {
        return CollectedOrder(
            partnerId = partnerId,
            orderUniqueKey = orderUniqueKey,
            bundleNumber = bundleNumber,
            storeName = storeName,
            storeCode = storeCode,
            storeId = storeId,
            collectedProductInfo = CollectedProductInfo(storeProductCode, storeProductName, storeProductOptionName),
            userStoreId = userStoreId,
            orderedAt = orderedAt,
            collectedAt = collectedAt,
            statusUpdatedAt = statusUpdatedAt,
            orderNumber = orderNumber,
            status = status,
            storeSyncStatus = StoreSyncStatus.NEW,
            claimStatus = claimStatus,
            deliveryType = deliveryType,
            expectedDeliveryDate = expectedDeliveryDate,
            price = price,
            shippingPrice = shippingPrice,
            quantity = count,
            receiver = receiver.toEntity(),
            uploadType = uploadType
        )
    }
}