package com.smartfoodnet.fnproduct.release.model.request

import com.smartfoodnet.fnproduct.order.model.OrderStatus
import io.swagger.annotations.ApiModelProperty

class ReleaseInfoSearchCondition(
    // ---------------------------------------------------------------------------------------------
    // -- CollectedOrder field
    // ---------------------------------------------------------------------------------------------
    @ApiModelProperty(value = "주문번호")
    var orderNumber: String? = null,

    @ApiModelProperty(value = "쇼핑몰 이름")
    var storeName: String? = null,

    @ApiModelProperty(value = "주문상태")
    var orderStatus: OrderStatus? = null,

    @ApiModelProperty(value = "받는분이름")
    var receiverName: String? = null,

    @ApiModelProperty(value = "업로드방식")
    var uploadType: String? = null,

    @ApiModelProperty(value = "클레임상태")
    var claimStatus: String? = null,

    // ---------------------------------------------------------------------------------------------
    // -- ReleaseInfo field
    // ---------------------------------------------------------------------------------------------
    @ApiModelProperty(value = "출고번호")
    var orderCode: String? = null,

    @ApiModelProperty(value = "송장번호")
    var shippingCode: String? = null,
)
