package com.smartfoodnet.fninventory.inbound.model.request

import com.smartfoodnet.fninventory.inbound.entity.Inbound
import com.smartfoodnet.fninventory.inbound.entity.InboundExpectedDetail
import com.smartfoodnet.fninventory.inbound.model.vo.InboundMethodType
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import io.swagger.annotations.ApiModelProperty
import org.hibernate.validator.constraints.Range
import org.springframework.validation.annotation.Validated
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Validated
data class InboundExpectedModel(
    @ApiModelProperty(value = "기본상품코드", example = "0014AA00012")
    @field:NotBlank(message = "기본상품코드를 입력하세요")
    val basicProductCode: String,

    @ApiModelProperty(value = "입고요청수량", example = "10")
    @field:NotNull(message = "입고요청수량을 입력하세요")
    @field:Range(min=1, max=9999, message = "요청수량은 0~9,999까지 가능합니다")
    val requestQuantity: Long = 0,

    @ApiModelProperty(value = "입고방식", example = "DELIVERY")
    @field:NotNull
    val inboundMethod: InboundMethodType? = null,

    @ApiModelProperty(value = "택배사 정보 기입여부", example = "false")
    val deliveryFlag: Boolean = false,

    @ApiModelProperty(value = "택배사", example = "SFN택배")
    val deliveryName: String? = null,

    @ApiModelProperty(value = "택배송장번호", example = "123456789")
    val trackingNo: String? = null
){
    fun toEntity(basicProduct : BasicProduct) = InboundExpectedDetail(
        basicProduct = basicProduct,
        requestQuantity = requestQuantity,
        method = inboundMethod,
        deliveryFlag = deliveryFlag,
        deliveryName = deliveryName,
        trackingNo = trackingNo
    )

}