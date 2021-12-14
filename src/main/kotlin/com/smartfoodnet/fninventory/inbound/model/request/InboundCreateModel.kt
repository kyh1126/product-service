package com.smartfoodnet.fninventory.inbound.model.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fninventory.inbound.entity.Inbound
import com.smartfoodnet.fninventory.inbound.model.vo.InboundMethodType
import com.smartfoodnet.fninventory.inbound.model.vo.InboundStatusType
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class InboundCreateModel(

    @ApiModelProperty(value = "화주(고객사) ID", example = "1")
    @field:NotNull(message = "화주(고객사) ID를 입력하세요")
    val partnerId: Long? = null,

    @ApiModelProperty(value = "기본상품코드", example = "0001AB00003")
    @field:NotBlank(message = "기본상품코드를 입력하세요")
    val basicProductCode: String? = null,

    @ApiModelProperty(value = "입고예정수량", example = "10")
    @field:NotNull
    val inboundRequestQuantity: Long? = null,

    @ApiModelProperty(value = "입고예정일자", example = "2021-12-03 13:22:33")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val inboundExpectedDate: LocalDateTime? = null,

    @ApiModelProperty(value = "입고방식", example = "DELIVERY")
    @field:NotNull
    val inboundMethod: InboundMethodType? = null,

    @ApiModelProperty(value = "택배사", example = "SFN택배")
    val deliveryName: String? = null,

    @ApiModelProperty(value = "택배송장번호", example = "123456789")
    val trackingNo: String? = null
) {

    fun toEntity(): Inbound {
        return run {
            Inbound(
                partnerId = partnerId,
                expectedDate = inboundExpectedDate,
                status = InboundStatusType.EXPECTED,
                method = inboundMethod,
                requestQuantity = inboundRequestQuantity,
                deliveryName = deliveryName,
                trackingNo = trackingNo
            )
        }
    }

}