package com.smartfoodnet.fnproduct.release.model.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimStatus
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
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
    var uploadType: OrderUploadType? = null,

    @ApiModelProperty(value = "클레임상태")
    var claimStatus: ClaimStatus? = null,

    // ---------------------------------------------------------------------------------------------
    // -- ReleaseInfo field
    // ---------------------------------------------------------------------------------------------
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    var partnerId: Long? = null,

    @ApiModelProperty(value = "출고번호")
    var orderCode: String? = null,

    @ApiModelProperty(value = "송장번호")
    var trackingNumber: String? = null,
)
