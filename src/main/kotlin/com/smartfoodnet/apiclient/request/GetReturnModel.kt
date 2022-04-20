package com.smartfoodnet.apiclient.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.common.Constants
import java.time.LocalDateTime

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetReturnModel(
    val releaseReturnInfoId: Long? = null,
    val memberId: Long? = null,
    val releaseId: Long? = null,
    val returnStatus: Int? = null,
    val returnCode: String? = null,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val requestDate: LocalDateTime? = null,
    val requestDt: String? = null,
    val requestMemberId: Long? = null,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val completeDate: LocalDateTime? = null,
    val completeDt: String? = null,
    val completeMemberId: Long? = null,
    val returnReasonId: Int? = null,
    val memo: String? = null,
    val receivingName: String? = null,
    val returnAddress1: String? = null,
    val returnAddress2: String? = null,
    val zipcode: String? = null,
    val tel1: String? = null,
    val tel2: String? = null,
    val returnProcessType: Int? = null,
    val deliveryAgencyId: Int? = null,
    val returnShippingCode: String? = null,
    val return_item_list: List<ReturnItem>? = null
)

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ReturnItem(
    val releaseReturnItemRenualId: Long,
    val releaseItemId: Long,
    val shippingProductId: Long,
    val quantity: Long,
    val unusableQuantity: Int? = null,
    val receivingQuantity: Int? = null,
    val expireDate: String? = null,
    val disposalExpireDate: String? = null
)
