package com.smartfoodnet.fnproduct.release.model.response

import com.smartfoodnet.fnproduct.order.entity.ConfirmProduct
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.entity.ReleaseProduct
import io.swagger.annotations.ApiModelProperty

data class OrderProductModel(
    @ApiModelProperty(value = "NOSNOS 발주 id")
    var orderId: Long,

    @ApiModelProperty(value = "출고번호")
    var orderCode: String,

    @ApiModelProperty(value = "주문번호")
    var orderNumbers: String,

    @ApiModelProperty(value = "NOSNOS 출고 id")
    var releaseId: Long? = null,

    @ApiModelProperty(value = "릴리즈코드")
    var releaseCode: String? = null,

    @ApiModelProperty(value = "출고상품명")
    var productName: String? = null,

    @ApiModelProperty(value = "출고상품코드")
    var productCode: String? = null,

    @ApiModelProperty(value = "출고상품수량")
    var quantity: Int
) {
    companion object {
        fun fromEntity(
            releaseProduct: ReleaseProduct,
            releaseInfo: ReleaseInfo,
        ): OrderProductModel {
            val collectedOrders = releaseInfo.confirmOrder?.requestOrderList
                ?.map { it.collectedOrder } ?: emptyList()

            return OrderProductModel(
                orderId = releaseInfo.orderId,
                orderCode = releaseInfo.orderCode,
                orderNumbers = collectedOrders.joinToString { it.orderNumber },
                releaseId = releaseInfo.releaseId,
                releaseCode = releaseInfo.releaseCode,
                productName = releaseProduct.basicProduct.name,
                productCode = releaseProduct.basicProduct.code,
                quantity = releaseProduct.quantity
            )
        }

        fun fromEntity(
            confirmProduct: ConfirmProduct,
            releaseInfo: ReleaseInfo,
        ): OrderProductModel {
            val collectedOrders = getCollectedOrders(releaseInfo)

            return OrderProductModel(
                orderId = releaseInfo.orderId,
                orderCode = releaseInfo.orderCode,
                orderNumbers = collectedOrders.joinToString { it.orderNumber },
                releaseId = releaseInfo.releaseId,
                releaseCode = releaseInfo.releaseCode,
                productName = confirmProduct.basicProduct.name,
                productCode = confirmProduct.basicProduct.code,
                quantity = confirmProduct.quantity
            )
        }

        private fun getCollectedOrders(releaseInfo: ReleaseInfo) =
            releaseInfo.confirmOrder?.requestOrderList
                ?.map { it.collectedOrder } ?: emptyList()
    }
}
