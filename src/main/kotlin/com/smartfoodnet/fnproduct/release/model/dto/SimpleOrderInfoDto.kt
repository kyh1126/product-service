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
    val orderNumbers: List<String>
) {
    companion object {
        fun from(orderId: Long, orderCode: String, collectedOrders: List<CollectedOrder>): SimpleOrderInfoDto {
            return SimpleOrderInfoDto(
                orderId = orderId,
                orderCode = orderCode,
                receiverModel = collectedOrders.first().receiver.run(ReceiverModel::from),
                orderNumbers = collectedOrders.map { it.orderNumber }
            )
        }
    }
}
