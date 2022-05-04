package com.smartfoodnet.apiclient.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.smartfoodnet.common.Constants.NOSNOS_CANCEL_REASON_NO
import com.smartfoodnet.common.Constants.NOSNOS_DATE_FORMAT
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.order.entity.ConfirmProduct
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.vo.PausedBy
import java.time.format.DateTimeFormatter

object RequestOrderMapper {

    fun toOutboundCreateBulkModel(confirmOrders: List<ConfirmOrder>): OutboundCreateBulkModel {
        val outboundBulkItemList = createOutboundBulkItem(confirmOrders)
        /**
         * 각 주문별로 중복되는 상품이 있는지 확인
         */
        outboundBulkItemList.forEach {
            searchDuplicateProduct(it)
        }

        return OutboundCreateBulkModel(
            confirmOrders.first().partnerId,
            outboundBulkItemList
        )
    }

    /**
     * 기본상품이 PACKAGE 인 경우 BASIC 단위로 풀어서 반환
     */
    private fun expandPackageProduct(confirmProduct: ConfirmProduct, bundleNumber: String): List<OrderItem> {
        val quantity = confirmProduct.quantity
        val basicProduct = confirmProduct.basicProduct
        return packageProductToOrderItemList(basicProduct, quantity, bundleNumber)
    }

    private fun packageProductToOrderItemList(basicProduct: BasicProduct, quantity: Int, bundleNumber: String): List<OrderItem> {
        return basicProduct.packageProductMappings.map {
            OrderItem(
                salesProductId = it.selectedBasicProduct.salesProductId
                    ?: throw BaseRuntimeException(errorMessage = "${it.selectedBasicProduct.name}의 salesProductId가 없습니다"),
                quantity = it.quantity * quantity,
                itemCd1 = bundleNumber,
                piecesPerBox = it.selectedBasicProduct.piecesPerBox ?: -1
            )
        }
    }

    private fun toOrderItemList(confirmProductList: List<ConfirmProduct>, bundleNumber: String): List<OrderItem> {
        val singleProduct = confirmProductList
            .filter { it.type != BasicProductType.PACKAGE }
            .map {
                OrderItem(
                    salesProductId = it.basicProduct.salesProductId
                        ?: throw BaseRuntimeException(errorMessage = "${it.basicProduct.name}의 salesProductId가 없습니다"),
                    quantity = it.quantity,
                    itemCd1 = bundleNumber,
                    piecesPerBox = it.basicProduct.piecesPerBox ?: -1
                )
            }

        val packageProduct = confirmProductList
            .filter { it.type == BasicProductType.PACKAGE }
            .map {
                expandPackageProduct(it, bundleNumber)
            }.flatten()

        return singleProduct + packageProduct
    }

    /**
     * 그룹핑 처리하여 1개 이상의 중복되는 상품은 합쳐서 보낸다
     */
    private fun searchDuplicateProduct(outboundBulkItem: OutboundCreateBulkItemModel) {
        val duplicateSalesIds = outboundBulkItem.orderItemList
            .groupBy { it.salesProductId }
            .map { sumDuplicateProduct(it.value) }

        outboundBulkItem.orderItemList = duplicateSalesIds
    }

    private fun sumDuplicateProduct(duplicateOrderItemList: List<OrderItem>): OrderItem {
        if (duplicateOrderItemList.count() == 1) {
            return duplicateOrderItemList.first()
        }

        val commonOrderItem = duplicateOrderItemList.first()
        var quantity: Int = 0
        duplicateOrderItemList.forEach {
            quantity += it.quantity
        }
        return OrderItem(commonOrderItem.salesProductId, quantity, commonOrderItem.itemCd1, null, commonOrderItem.piecesPerBox)
    }

    private fun createOutboundBulkItem(confirmOrders: List<ConfirmOrder>): List<OutboundCreateBulkItemModel> {
        return confirmOrders.map { confirmOrder ->
            confirmOrder.run {
                OutboundCreateBulkItemModel(
                    companyOrderCode = requestOrderList.first().collectedOrder.orderNumber,
                    shippingMethodId = shippingMethodType,
                    requestShippingDt = requestShippingDate.format(DateTimeFormatter.ofPattern(NOSNOS_DATE_FORMAT)),
                    buyerName = receiver.name,
                    receiverName = receiver.name,
                    tel1 = receiver.phoneNumber,
                    zipcode = receiver.zipCode,
                    shippingAddress1 = receiver.address,
                    shippingMessage = shippingMessage,
                    memo1 = memo?.memo1,
                    memo2 = memo?.memo2,
                    memo3 = memo?.memo3,
                    memo4 = memo?.memo4,
                    memo5 = memo?.memo5,
                    orderItemList = toOrderItemList(
                        requestOrderList
                            .map { it.collectedOrder.confirmProductList }
                            .flatten(), bundleNumber
                    )
                )
            }
        }
    }

}

class OutboundCreateModel(
    @JsonProperty("partner_id")
    val partnerId: Long? = null,

    @JsonProperty("company_order_code")
    val companyOrderCode: String,

    @JsonProperty("shipping_method_id")
    val shippingMethodId: Int? = null,

    @JsonProperty("request_shipping_dt")
    val requestShippingDt: String,

    @JsonProperty("buyer_name")
    val buyerName: String? = null,

    @JsonProperty("receiver_name")
    val receiverName: String,

    @JsonProperty("tel1")
    val tel1: String,

    @JsonProperty("tel2")
    val tel2: String? = null,

    @JsonProperty("zipcode")
    val zipcode: String? = null,

    @JsonProperty("shipping_address1")
    val shippingAddress1: String,

    @JsonProperty("shipping_address2")
    val shippingAddress2: String? = null,

    @JsonProperty("shipping_message")
    val shippingMessage: String? = null,

    @JsonProperty("channel_id")
    val channelId: Int? = null,

    @JsonProperty("memo1")
    val memo1: String? = null,

    @JsonProperty("memo2")
    val memo2: String? = null,

    @JsonProperty("memo3")
    val memo3: String? = null,

    @JsonProperty("memo4")
    val memo4: String? = null,

    @JsonProperty("memo5")
    val memo5: String? = null,

    @JsonProperty("order_item_list")
    val orderItemList: List<OrderItem>
)

class OrderItem(
    @JsonProperty("sales_product_id")
    val salesProductId: Long,
    @JsonProperty("quantity")
    val quantity: Int,
    @JsonProperty("item_cd1")
    val itemCd1: String? = null,
    @JsonProperty("item_cd2")
    val itemCd2: String? = null,
    @JsonIgnore
    val piecesPerBox: Int = -1
) {
    @JsonProperty("item_cd3")
    val itemCd3: String? = if ((quantity / piecesPerBox) == 1) "단수" else null
}

class OutboundCreateBulkModel(
    @JsonProperty("partner_id")
    val partnerId: Long? = null,

    @JsonProperty("request_data_list")
    val requestDataList: List<OutboundCreateBulkItemModel>
)

class OutboundCreateBulkItemModel(
    @JsonProperty("company_order_code")
    val companyOrderCode: String,

    @JsonProperty("shipping_method_id")
    val shippingMethodId: Int? = null,

    @JsonProperty("request_shipping_dt")
    val requestShippingDt: String,

    @JsonProperty("buyer_name")
    val buyerName: String? = null,

    @JsonProperty("receiver_name")
    val receiverName: String,

    @JsonProperty("tel1")
    val tel1: String,

    @JsonProperty("tel2")
    val tel2: String? = null,

    @JsonProperty("zipcode")
    val zipcode: String? = null,

    @JsonProperty("shipping_address1")
    val shippingAddress1: String,

    @JsonProperty("shipping_address2")
    val shippingAddress2: String? = null,

    @JsonProperty("shipping_message")
    val shippingMessage: String? = null,

    @JsonProperty("channel_id")
    val channelId: Int? = null,

    @JsonProperty("memo1")
    val memo1: String? = null,

    @JsonProperty("memo2")
    val memo2: String? = null,

    @JsonProperty("memo3")
    val memo3: String? = null,

    @JsonProperty("memo4")
    val memo4: String? = null,

    @JsonProperty("memo5")
    val memo5: String? = null,

    @JsonProperty("order_item_list")
    var orderItemList: List<OrderItem>
)

class OutboundCancelModel(
    @JsonProperty("partner_id")
    val partnerId: Long? = null,

    @JsonProperty("order_id")
    val orderId: Long,

    @JsonProperty("cancel_reason_no")
    val cancelReasonNo: Int,

    @JsonProperty("cancel_reason_content")
    val cancelReasonContent: String? = null,
) {
    companion object {
        fun fromEntity(releaseInfo: ReleaseInfo): OutboundCancelModel {
            return releaseInfo.run {
                OutboundCancelModel(
                    partnerId = partnerId,
                    orderId = orderId,
                    cancelReasonNo = NOSNOS_CANCEL_REASON_NO,
                    cancelReasonContent = "${PausedBy.PARTNER.description} 발주삭제 요청"
                )
            }
        }
    }
}
