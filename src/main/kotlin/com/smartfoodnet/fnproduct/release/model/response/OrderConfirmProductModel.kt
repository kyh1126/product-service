package com.smartfoodnet.fnproduct.release.model.response

import com.smartfoodnet.fnproduct.order.dto.ConfirmProductModel
import io.swagger.annotations.ApiModelProperty

data class OrderConfirmProductModel(
    @ApiModelProperty(value = "매칭상품 id")
    var confirmProductId: Long,

    @ApiModelProperty(value = "주문번호")
    var orderNumber: String,

    @ApiModelProperty(value = "매칭상품명")
    var basicProductName: String? = null,

    @ApiModelProperty(value = "매칭상품코드")
    var basicProductCode: String? = null,

    @ApiModelProperty(value = "주문수량(매칭상품기준)")
    var mappedQuantity: Int
) {
    companion object {
        fun from(confirmProductModel: ConfirmProductModel): OrderConfirmProductModel {
            return confirmProductModel.run {
                OrderConfirmProductModel(
                    confirmProductId = confirmProductId,
                    orderNumber = orderNumber,
                    basicProductName = basicProductName,
                    basicProductCode = basicProductShippingProductCode,
                    mappedQuantity = releaseQuantity
                )
            }
        }
    }
}
