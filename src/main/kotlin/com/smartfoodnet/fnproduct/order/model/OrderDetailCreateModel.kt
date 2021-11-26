package com.smartfoodnet.fnproduct.order.model

import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class OrderDetailCreateModel(
    @ApiModelProperty(value = "화주사 ID")
    val partnerId: Long? = null,

    @ApiModelProperty(value = "중복처리를 위한 쇼핑몰 종속적 유니크 키")
    val orderUniqueKey: String? = null,

    @ApiModelProperty(value = "쇼핑몰 이름")
    val storeName: String? = null,

    @ApiModelProperty(value = "쇼핑몰 코드(플레이오토 쇼핑몰 코드)")
    val omsStoreCode: String? = null,

    @ApiModelProperty(value = "화주사 쇼핑몰 ID")
    val userStoreId: String? = null,

    @ApiModelProperty(value = "주문번호")
    val orderNumber: String? = null,

    @ApiModelProperty(value = "쇼핑몰 상품명")
    val storeProductName: String? = null,

    @ApiModelProperty(value = "쇼핑몰별 상품코드")
    val storeProductCode: String? = null,

    @ApiModelProperty(value = "상품 옵션명")
    val storeProductOptionName: String? = null,

    @ApiModelProperty(value = "주문일자")
    val orderedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "주문 상태")
    val status: OrderStatus? = null,

    @ApiModelProperty(value = "클레임 상태")
    val claimStatus: String? = null,

    @ApiModelProperty(value = "배송방식")
    val deliveryType: String? = null,

    @ApiModelProperty(value = "주문금액")
    val price: Double? = null,

    @ApiModelProperty(value = "배송비")
    val shippingPrice: Double? = null,

    val receiver: ReceiverModel? = null,

    @ApiModelProperty(value = "업로드방식")
    val uploadType: String? = null
)