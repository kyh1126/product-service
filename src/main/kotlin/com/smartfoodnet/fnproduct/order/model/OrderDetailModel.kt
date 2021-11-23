package com.smartfoodnet.fnproduct.order.model

import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class OrderDetailModel(

    val id: Long? = null,

    @ApiModelProperty(value = "화주사 ID")
    val partnerId: Long? = null,

    @ApiModelProperty(value = "중복처리를 위한 쇼핑몰 종속적 유니크 키")
    val storeSpecificUniqueKey: String? = null,

    @ApiModelProperty(value = "불러오기 상태")
    val loadStatus: String? = null,

    @ApiModelProperty(value = "쇼핑몰 이름")
    val storeName: String? = null,

    @ApiModelProperty(value = "쇼핑몰 코드(SFN 기준 코드)")
    val storeCode: String? = null,

    @ApiModelProperty(value = "화주사 쇼핑몰 ID")
    val userStoreId: String? = null,

    @ApiModelProperty(value = "주문일자")
    val orderedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "주문번호")
    val orderNumber: String? = null,

    @ApiModelProperty(value = "주문 상태")
    val status: String? = null,

    @ApiModelProperty(value = "클레임 상태")
    val claimStatus: String? = null,

    @ApiModelProperty(value = "배송방식")
    val deliveryType: String? = null,

    @ApiModelProperty(value = "배송완료요청일")
    val desiredDeliveryDate: LocalDateTime? = null,

    @ApiModelProperty(value = "쇼핑몰 상품 ID")
    val storeProductId: Long? = null,

    @ApiModelProperty(value = "주문금액")
    val price: Double? = null,

    @ApiModelProperty(value = "배송비")
    val shippingPrice: String? = null,

    val receiver: ReceiverModel? = null,

    val sender: SenderModel? = null,

    @ApiModelProperty(value = "업로드방식")
    val uploadType: String? = null
)