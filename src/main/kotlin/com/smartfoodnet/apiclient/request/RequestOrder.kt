package com.smartfoodnet.apiclient.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.smartfoodnet.common.Constants.NOSNOS_DATE_FORMAT
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.order.entity.ConfirmProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import java.time.format.DateTimeFormatter

object RequestOrderMapper {
    private fun expandPackageProduct(confirmProduct: ConfirmProduct, bundleNumber: String): List<OrderItem> {
        val quantity = confirmProduct.quantity
        val basicProduct = confirmProduct.basicProduct
        return basicProduct.packageProductMappings.map {
            OrderItem(
                salesProductId = it.selectedBasicProduct.salesProductId
                    ?: throw BaseRuntimeException(errorMessage = "${it.selectedBasicProduct.name}의 salesProductId가 없습니다"),
                quantity = it.quantity * quantity,
                itemCd1 = bundleNumber,
                itemCd3 = isBoxShipping(it.quantity * quantity, it.selectedBasicProduct.piecesPerBox?:-1)
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
                    itemCd3 = isBoxShipping(it.quantity, it.basicProduct.piecesPerBox?:-1)
                )
            }

        val packageProduct = confirmProductList
            .filter { it.type == BasicProductType.PACKAGE }
            .map {
                expandPackageProduct(it, bundleNumber)
            }.flatten()

        return singleProduct + packageProduct
    }

    private fun isBoxShipping(quantity: Int, piecesPerBox : Int) : String? {
        return if ((quantity / piecesPerBox) == 1) "단수" else null
    }

    fun toOutboundCreateModel(confirmOrder: ConfirmOrder): OutboundCreateModel {
        return confirmOrder.run {
            OutboundCreateModel(
                partnerId = partnerId,
                companyOrderCode = requestOrderList.first().collectedOrder.orderNumber,
                shippingMethodId = shippingMethodType,
                requestShippingDt = requestShippingDate.format(DateTimeFormatter.ofPattern(NOSNOS_DATE_FORMAT)),
                buyerName = receiver.name,
                receiverName = receiver.name,
                tel1 = receiver.phoneNumber,
                // TODO : 2022-03-16 주문수집시 우편번호도 같이 들어와야합니다
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
                        .flatten()
                    ,bundleNumber
                )
            )
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
    @JsonProperty("item_cd3")
    val itemCd3: String? = null
)
