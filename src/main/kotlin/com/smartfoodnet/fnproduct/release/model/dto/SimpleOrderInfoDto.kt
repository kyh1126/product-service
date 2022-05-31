package com.smartfoodnet.fnproduct.release.model.dto

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.model.ReceiverModel
import io.swagger.annotations.ApiModelProperty

data class SimpleOrderInfoDto(
    @ApiModelProperty(value = "NOSNOS 발주 id")
    var orderId: Long,

    @ApiModelProperty(value = "출고번호")
    var orderCode: String,

    @JsonUnwrapped
    val receiverModel: ReceiverModel,

    @ApiModelProperty(value = "주문번호")
    val orderNumbers: List<String>,

    @ApiModelProperty(value = "쇼핑몰 이름")
    var storeName: String,

    @ApiModelProperty(value = "쇼핑몰 아이콘")
    var storeIcon: String? = null,

    @ApiModelProperty(value = "묶음번호")
    var bundleNumber: String,
) {
    companion object {
        fun from(
            orderId: Long,
            orderCode: String,
            collectedOrders: List<CollectedOrder>
        ): SimpleOrderInfoDto {
            val firstCollectedOrder = collectedOrders.first()

            return SimpleOrderInfoDto(
                orderId = orderId,
                orderCode = orderCode,
                receiverModel = firstCollectedOrder.receiver.run(ReceiverModel::from),
                orderNumbers = collectedOrders.map { it.orderNumber },
                storeName = firstCollectedOrder.storeName,
                storeIcon = firstCollectedOrder.storeIcon,
                bundleNumber = firstCollectedOrder.bundleNumber
            )
        }
    }
}
