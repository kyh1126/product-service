package com.smartfoodnet.fninventory.inbound.model.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fninventory.inbound.entity.Inbound
import com.smartfoodnet.fninventory.inbound.model.vo.InboundMethodType
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class InboundModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "기본상품")
    val basicProduct: InboundBasicProductModel? = null,

    @ApiModelProperty(value = "입고예정수량")
    val requestQuantity: Long? = null,

    @ApiModelProperty(value = "입고예정일자")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val expectedDate: LocalDateTime? = null,

    @ApiModelProperty(value = "입고방식")
    val inboundMethod: InboundMethodType? = null,

    @ApiModelProperty(value = "택배사")
    val deliveryName: String? = null,

    @ApiModelProperty(value = "택배송장번호")
    val trackingNo: String? = null,
) {
    companion object {
        fun fromEntity(inbound: Inbound): InboundModel {
            return InboundModel(
                id = inbound.id,
                basicProduct = InboundBasicProductModel.fromEntity(inbound.basicProduct),
                requestQuantity = inbound.requestQuantity,
                expectedDate = inbound.expectedDate,
                inboundMethod = inbound.method,
                deliveryName = inbound.deliveryName,
                trackingNo = inbound.trackingNo
            )
        }
    }
}

data class InboundBasicProductModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "기본상품명")
    val productName: String? = null,

    @ApiModelProperty(value = "기본상품코드")
    val productCode: String? = null,
) {
    companion object {
        fun fromEntity(basicProduct: BasicProduct?): InboundBasicProductModel? {
            return if (basicProduct == null) {
                null
            } else {
                InboundBasicProductModel(
                    id = basicProduct.id,
                    productName = basicProduct.name,
                    productCode = basicProduct.code
                )
            }
        }
    }
}