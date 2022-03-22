package com.smartfoodnet.fnproduct.order.model.response

import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import io.swagger.annotations.ApiModelProperty

data class ManualReleaseResponseModel(
    @ApiModelProperty(value = "collected order(주문외출고)")
    val collectedOrder: ManualReleaseCollectedOrder? = null,
    @ApiModelProperty(value = "confirm orders(발주등록)")
    val confirmOrders: List<ManualReleaseConfirmOrder> = mutableListOf(),
) {

    data class ManualReleaseCollectedOrder(
        @ApiModelProperty(value = "collected order id")
        val id: Long?,
        @ApiModelProperty(value = "sfn생성 주문번호")
        val orderNumber: String?,
        @ApiModelProperty(value = "sfn생성 묶음번호")
        val bundleNumber: String?
    )

    data class ManualReleaseConfirmOrder(
        val id: Long?,
        val nosnosOrderId: Long? = null,
        val nosnosOrderCode: String? = null,
    )

    companion object {
        fun from(
            collectedOrder: CollectedOrder,
            confirmOrders: List<ConfirmOrder>,
        ): ManualReleaseResponseModel {
            return ManualReleaseResponseModel(
                collectedOrder = ManualReleaseCollectedOrder(
                    id = collectedOrder.id,
                    orderNumber = collectedOrder.orderNumber,
                    bundleNumber = collectedOrder.bundleNumber,
                ),
                confirmOrders = confirmOrders.map {
                    ManualReleaseConfirmOrder(
                        id = it.id,
                        nosnosOrderId = it.orderId,
                        nosnosOrderCode = it.orderCode,
                    )
                }
            )
        }
    }
}
