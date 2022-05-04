package com.smartfoodnet.fnproduct.order.model

import com.smartfoodnet.fnproduct.order.entity.Receiver
import io.swagger.annotations.ApiModelProperty

class ReceiverModel(
    @ApiModelProperty(value = "받는 분 이름")
    val name: String,
    @ApiModelProperty(value = "받는 분 우편번호")
    val zipCode: String? = null,
    @ApiModelProperty(value = "받는 분 주소")
    val address: String,
    @ApiModelProperty(value = "받는 분 전화번호")
    val phoneNumber: String,
) {
    fun toEntity(): Receiver {
        return run {
            Receiver(name = name, zipCode = zipCode, address = address, phoneNumber = phoneNumber)
        }
    }

    companion object {
        fun from(receiver: Receiver): ReceiverModel {
            return receiver.run {
                ReceiverModel(name = name, zipCode = zipCode, address = address, phoneNumber = phoneNumber)
            }
        }
    }
}
