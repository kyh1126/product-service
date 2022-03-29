package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ReturnCreateModel(
    var partnerId: Long? = null,
    var memberId: Long? = null,
    val releaseId: Int? = null,
    val returnReasonId: Int? = null,
    val memo: String? = null,
    val receivingName: String? = null,
    val returnAddress1: String? = null,
    val returnAddress2: String? = null,
    val zipcode: String? = null,
    val tel1: String? = null,
    val tel2: String? = null,
    val releaseItemList: List<ReturnCreateItem>?
)
