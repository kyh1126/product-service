package com.smartfoodnet.fnproduct.order.model.response

import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import io.swagger.annotations.ApiModelProperty

data class ManualOrderResponseModel(
    @ApiModelProperty(value = "collected order(주문외출고)")
    val collectedOrder: ManualReleaseCollectedOrder? = null,
    @ApiModelProperty(value = "confirm orders(발주등록)")
    val confirmOrder: ManualConfirmOrder? = null,
) {

    data class ManualReleaseCollectedOrder(
        @ApiModelProperty(value = "collected order id")
        val id: Long?,
        @ApiModelProperty(value = "sfn생성 주문번호")
        val orderNumber: String?,
        @ApiModelProperty(value = "sfn생성 묶음번호")
        val bundleNumber: String?
    )

    data class ManualConfirmOrder(
        val id: Long?,
        val nosnosOrderId: Long? = null,
        val nosnosOrderCode: String? = null,
    )

    companion object {
        fun from(collectedOrder: CollectedOrder, confirmOrder: ConfirmOrder): ManualOrderResponseModel {
            return ManualOrderResponseModel(
                collectedOrder = ManualReleaseCollectedOrder(
                    id = collectedOrder.id,
                    orderNumber = collectedOrder.orderNumber,
                    bundleNumber = collectedOrder.bundleNumber,
                ),
                confirmOrder = ManualConfirmOrder(
                    id = confirmOrder.id,
                    nosnosOrderId = confirmOrder.orderId,
                    nosnosOrderCode = confirmOrder.orderCode,
                )
            )
        }
    }
}
