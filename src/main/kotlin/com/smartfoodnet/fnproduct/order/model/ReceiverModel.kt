package com.smartfoodnet.fnproduct.order.model

import com.smartfoodnet.fnproduct.order.entity.Receiver
import io.swagger.annotations.ApiModelProperty
import javax.persistence.Embeddable

@Embeddable
class ReceiverModel(
        @ApiModelProperty(value = "받는 분 이름")
        var name: String? = null,
        @ApiModelProperty(value = "받는 분 주소")
        var address: String? = null,
        @ApiModelProperty(value = "받는 분 전화번호")
        var phoneNumber: String? = null,
) {
    fun toEntity(): Receiver {
        return run {
            Receiver(
                    name = name,
                    address = address,
                    phoneNumber = phoneNumber
            )
        }
    }
}