package com.smartfoodnet.fnproduct.release.model.response

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.entity.ConfirmProduct
import com.smartfoodnet.fnproduct.order.model.ReceiverModel
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.entity.ReleaseProduct
import com.smartfoodnet.fnproduct.release.model.vo.PausedBy
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class PausedOrderProductModel(
    @ApiModelProperty(value = "NOSNOS 발주 id")
    var orderId: Long,

    @ApiModelProperty(value = "출고번호")
    var orderCode: String,

    @ApiModelProperty(value = "NOSNOS 출고 id")
    var releaseId: Long? = null,

    @ApiModelProperty(value = "릴리즈코드")
    var releaseCode: String? = null,

    @JsonUnwrapped
    var receiverModel: ReceiverModel,

    @ApiModelProperty(value = "주문번호")
    var orderNumbers: List<String>,

    @ApiModelProperty(value = "출고상태")
    var orderStatus: OrderStatus,

    @ApiModelProperty(value = "주문수집일시")
    var collectedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "출고중지일시")
    var pausedAt: LocalDateTime,

    @ApiModelProperty(value = "출고중지사유")
    var pausedReason: String? = null,

    @ApiModelProperty(value = "출고중지당사자")
    var pausedBy: PausedBy? = null,

    @ApiModelProperty(value = "기존 출고번호")
    var previousOrderCode: String? = null,

    @ApiModelProperty(value = "재출고 출고번호")
    var nextOrderCode: String? = null,

    @ApiModelProperty(value = "기본상품 ID")
    var basicProductId: Long,

    @ApiModelProperty(value = "출고상품명")
    var basicProductName: String,

    @ApiModelProperty(value = "출고상품코드")
    var basicProductCode: String,

    @ApiModelProperty(value = "출고상품수량")
    var quantity: Int,

    @ApiModelProperty(value = "상품별주문수")
    var orderCount: Int
) {
    companion object {
        fun fromEntity(
            releaseProduct: ReleaseProduct,
            releaseInfo: ReleaseInfo,
        ): PausedOrderProductModel {
            val collectedOrders = getCollectedOrders(releaseInfo)
            val firstCollectedOrder = collectedOrders.first()

            return releaseInfo.run {
                PausedOrderProductModel(
                    orderId = orderId,
                    orderCode = orderCode,
                    releaseId = releaseId,
                    releaseCode = releaseCode,
                    receiverModel = firstCollectedOrder.receiver.run(ReceiverModel::from),
                    orderNumbers = collectedOrders.map { it.orderNumber },
                    orderStatus = releaseStatus.orderStatus,
                    collectedAt = firstCollectedOrder.collectedAt,
                    pausedAt = pausedAt!!,
                    pausedReason = pausedReason,
                    pausedBy = pausedBy,
                    previousOrderCode = previousOrderCode,
                    nextOrderCode = nextOrderCode,
                    basicProductId = releaseProduct.basicProduct.id,
                    basicProductName = releaseProduct.basicProduct.name,
                    basicProductCode = releaseProduct.basicProduct.code!!,
                    quantity = releaseProduct.quantity,
                    orderCount = getOrderCount(releaseProduct.basicProduct.id, collectedOrders)
                )
            }
        }

        fun fromEntity(
            confirmProduct: ConfirmProduct,
            releaseInfo: ReleaseInfo,
        ): PausedOrderProductModel {
            val collectedOrders = getCollectedOrders(releaseInfo)
            val firstCollectedOrder = collectedOrders.first()

            return releaseInfo.run {
                PausedOrderProductModel(
                    orderId = orderId,
                    orderCode = orderCode,
                    releaseId = releaseId,
                    releaseCode = releaseCode,
                    receiverModel = firstCollectedOrder.receiver.run(ReceiverModel::from),
                    orderNumbers = collectedOrders.map { it.orderNumber },
                    orderStatus = releaseStatus.orderStatus,
                    collectedAt = firstCollectedOrder.collectedAt,
                    pausedAt = pausedAt!!,
                    pausedReason = pausedReason,
                    pausedBy = pausedBy,
                    previousOrderCode = previousOrderCode,
                    nextOrderCode = nextOrderCode,
                    basicProductId = confirmProduct.basicProduct.id,
                    basicProductName = confirmProduct.basicProduct.name,
                    basicProductCode = confirmProduct.basicProduct.code!!,
                    quantity = confirmProduct.quantity,
                    orderCount = getOrderCount(confirmProduct.basicProduct.id, collectedOrders)
                )
            }
        }

        private fun getCollectedOrders(releaseInfo: ReleaseInfo) =
            releaseInfo.confirmOrder?.requestOrderList
                ?.map { it.collectedOrder } ?: emptyList()

        private fun getOrderCount(basicProductId: Long, collectedOrders: List<CollectedOrder>): Int {
            return collectedOrders.count { it.containsBasicProduct(basicProductId) }
        }
    }
}
