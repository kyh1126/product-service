package com.smartfoodnet.fnproduct.release.model.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.fnproduct.claim.model.vo.ExchangeStatus
import com.smartfoodnet.fnproduct.claim.model.vo.ReturnStatus
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import com.smartfoodnet.fnproduct.release.model.vo.ReleaseStatus
import io.swagger.annotations.ApiModelProperty

class ReleaseInfoSearchCondition(
    // ---------------------------------------------------------------------------------------------
    // -- Claim field
    // ---------------------------------------------------------------------------------------------
    @ApiModelProperty(value = "반품상태")
    var returnStatus: ReturnStatus? = null,

    @ApiModelProperty(value = "교환출고상태")
    var exchangeStatus: ExchangeStatus? = null,

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

    @ApiModelProperty(value = "수집방식")
    var uploadType: OrderUploadType? = null,

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
) {
    fun setPausedOrderStatus() {
        orderStatus = OrderStatus.RELEASE_PAUSED
    }
}

class ReleaseStatusSearchCondition(
    var partnerId: Long? = null,
    var deliveryAgencyId: Long? = null,
    var orderCode: String? = null,
    var releaseCode: String? = null,
    var releaseStatuses: Collection<ReleaseStatus>,
)
