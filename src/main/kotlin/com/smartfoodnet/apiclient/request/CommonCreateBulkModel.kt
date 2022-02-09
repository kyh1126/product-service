package com.smartfoodnet.apiclient.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CommonCreateBulkModel<T>(
    val partnerId: Long? = null,
    val memberId: Long? = null,
    val requestDataList: List<T>
)
