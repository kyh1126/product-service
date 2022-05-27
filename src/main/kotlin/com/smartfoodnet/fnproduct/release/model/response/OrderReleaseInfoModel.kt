package com.smartfoodnet.fnproduct.release.model.response

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.release.model.dto.SimpleOrderInfoDto
import io.swagger.annotations.ApiModelProperty

data class OrderReleaseInfoModel(
    @JsonUnwrapped
    val simpleOrderInfo: SimpleOrderInfoDto? = null,
    @ApiModelProperty(value = "출고상품 정보 리스트")
    val orderProducts: List<OrderProductModel>
) {
    companion object {
        fun fromModel(orderProducts: List<OrderProductModel>): OrderReleaseInfoModel {
            return OrderReleaseInfoModel(orderProducts.firstOrNull()?.simpleOrderInfo, orderProducts)
        }
    }
}
