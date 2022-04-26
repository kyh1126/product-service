package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NosnosDeliveryAgencyInfoModel(
    val deliveryAgencyId: Long? = null,
    val deliveryAgencyName: String? = null,
)
