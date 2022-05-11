package com.smartfoodnet.fnproduct.release.model.response

import com.smartfoodnet.fnproduct.order.entity.ConfirmProduct
import com.smartfoodnet.fnproduct.order.vo.DeliveryType
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.entity.ReleaseProduct
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class OrderProductModel(
    @ApiModelProperty(value = "NOSNOS 발주 id")
    var orderId: Long,

    @ApiModelProperty(value = "출고번호")
    var orderCode: String,

    @ApiModelProperty(value = "주문번호")
    var orderNumbers: List<String>,

    @ApiModelProperty(value = "배송방식")
    val deliveryType: DeliveryType,

    @ApiModelProperty(value = "NOSNOS 출고 id")
    var releaseId: Long? = null,

    @ApiModelProperty(value = "릴리즈코드")
    var releaseCode: String? = null,

    @ApiModelProperty(value = "출고상태")
    var orderStatus: OrderStatus,

    @ApiModelProperty(value = "송장번호")
    var trackingNumber: String? = null,

    @ApiModelProperty(value = "송장번호부여일시")
    var trackingNumberCreatedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "기본상품 ID")
    var basicProductId: Long? = null,

    @ApiModelProperty(value = "출고상품명")
    var basicProductName: String? = null,

    @ApiModelProperty(value = "출고상품코드")
    var basicProductCode: String? = null,

    @ApiModelProperty(value = "출고상품수량")
    var quantity: Int
) {
    companion object {
        fun fromEntity(
            releaseProduct: ReleaseProduct,
            releaseInfo: ReleaseInfo,
        ): OrderProductModel {
            val collectedOrders = getCollectedOrders(releaseInfo)
            val firstCollectedOrder = collectedOrders.first()

            return releaseInfo.run {
                OrderProductModel(
                    orderId = orderId,
                    orderCode = orderCode,
                    orderNumbers = collectedOrders.map { it.orderNumber },
                    deliveryType = firstCollectedOrder.deliveryType,
                    releaseId = releaseId,
                    releaseCode = releaseCode,
                    orderStatus = releaseStatus.orderStatus,
                    trackingNumber = trackingNumber,
                    trackingNumberCreatedAt = trackingNumberCreatedAt,
                    basicProductId = releaseProduct.basicProduct.id,
                    basicProductName = releaseProduct.basicProduct.name,
                    basicProductCode = releaseProduct.basicProduct.code,
                    quantity = releaseProduct.quantity
                )
            }
        }

        fun fromEntity(
            confirmProduct: ConfirmProduct,
            releaseInfo: ReleaseInfo,
        ): OrderProductModel {
            val collectedOrders = getCollectedOrders(releaseInfo)
            val firstCollectedOrder = collectedOrders.first()

            return releaseInfo.run {
                OrderProductModel(
                    orderId = orderId,
                    orderCode = orderCode,
                    orderNumbers = collectedOrders.map { it.orderNumber },
                    deliveryType = firstCollectedOrder.deliveryType,
                    releaseId = releaseId,
                    releaseCode = releaseCode,
                    orderStatus = releaseStatus.orderStatus,
                    trackingNumber = trackingNumber,
                    trackingNumberCreatedAt = trackingNumberCreatedAt,
                    basicProductId = confirmProduct.basicProduct.id,
                    basicProductName = confirmProduct.basicProduct.name,
                    basicProductCode = confirmProduct.basicProduct.code,
                    quantity = confirmProduct.quantity
                )
            }
        }

        private fun getCollectedOrders(releaseInfo: ReleaseInfo) =
            releaseInfo.confirmOrder?.requestOrderList
                ?.map { it.collectedOrder } ?: emptyList()
    }
}
