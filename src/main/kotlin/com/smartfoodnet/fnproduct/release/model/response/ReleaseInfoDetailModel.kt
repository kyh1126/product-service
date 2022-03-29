package com.smartfoodnet.fnproduct.release.model.response

import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.vo.ShippingCodeStatus
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class ReleaseInfoDetailModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "출고코드")
    var orderCode: String? = null,

    @ApiModelProperty(value = "릴리즈코드")
    var releaseCode: String? = null,

    @ApiModelProperty(value = "묶음번호")
    var bundleNumbers: String? = null,

    @ApiModelProperty(value = "주문번호")
    var orderNumbers: String? = null,

    @ApiModelProperty(value = "주문상태")
    var orderStatuses: List<OrderStatus> = emptyList(),

    @ApiModelProperty(value = "송장번호")
    var shippingCode: String? = null,

    @ApiModelProperty(value = "쇼핑몰송장등록상태")
    var shippingCodeStatus: ShippingCodeStatus? = null,

    @ApiModelProperty(value = "송장번호부여일시")
    var shippingCodeCreatedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "배송완료일시")
    var deliveryCompletedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "업로드방식")
    var uploadType: String? = null,

    @ApiModelProperty(value = "클레임상태")
    var claimStatus: String? = null,

    @ApiModelProperty(value = "쇼핑몰 이름")
    var storeName: String? = null,

    @ApiModelProperty(value = "받는분이름")
    var receiverName: String? = null,

    @ApiModelProperty(value = "받는분주소")
    var receiverAddress: String? = null,

    @ApiModelProperty(value = "받는분휴대전화")
    var phoneNumber: String? = null,
) {
    companion object {
        fun fromEntity(releaseInfo: ReleaseInfo, collectedOrders: List<CollectedOrder>): ReleaseInfoDetailModel {
            return releaseInfo.run {
                val receivers = collectedOrders.map { it.receiver }
                ReleaseInfoDetailModel(
                    id = id,
                    orderCode = orderCode,
                    releaseCode = releaseCode,
                    shippingCode = shippingCode,
                    shippingCodeStatus = shippingCodeStatus,
                    shippingCodeCreatedAt = shippingCodeCreatedAt,
                    deliveryCompletedAt = deliveryCompletedAt,
                    // TODO: 모델로 빼서 Flatten 하거나 목록 조회 API 만들면서 객체지향적으로 구조 같이 변경하기
                    bundleNumbers = collectedOrders.joinToString { it.bundleNumber },
                    orderNumbers = collectedOrders.joinToString { it.orderNumber as String },
                    orderStatuses = collectedOrders.map { it.status },
                    uploadType = collectedOrders.joinToString { it.uploadType as String },
                    claimStatus = collectedOrders.joinToString { it.claimStatus as String },
                    storeName = collectedOrders.joinToString { it.storeName },
                    receiverName = receivers.joinToString { it?.name ?: "" },
                    receiverAddress = receivers.joinToString { it?.address ?: "" },
                    phoneNumber = receivers.joinToString { it?.phoneNumber ?: "" },
                )
            }
        }
    }
}
