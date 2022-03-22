package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetReleaseModel(
    val releaseId: Int? = null,
    val memberId: Int? = null,
    val releaseCode: String? = null,
    val orderId: Int? = null,
    val orderCode: String? = null,
    val companyOrderCode: String? = null,
    val shippingMethodId: Int? = null,
    val requestShippingDt: String? = null,
    val releaseDate: String? = null,
    val releaseStatus: Int? = null,
    val shippingOrderInfoId: Int? = null,
    val deliveryAgencyId: Int? = null,
    val shippingCode: String? = null,
    val etc1: String? = null,
    val etc2: String? = null,
    val etc3: String? = null,
    val etc4: String? = null,
    val etc5: String? = null,
    val etc6: String? = null,
    val buyerName: String? = null,
    val receiverName: String? = null,
    val tel1: String? = null,
    val tel2: String? = null,
    val zipcode: String? = null,
    val shippingAddress1: String? = null,
    val shippingAddress2: String? = null,
    val shippingMessage: String? = null,
    val channelId: Int? = null,
)
