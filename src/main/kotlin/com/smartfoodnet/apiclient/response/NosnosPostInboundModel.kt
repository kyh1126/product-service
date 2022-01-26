package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.annotation.JsonProperty

class NosnosPostInboundModel (
    @JsonProperty("receiving_plan_code")
    val receivingPlanCode: String?,
    @JsonProperty("receiving_plan_id")
    val receivingPlanId: Long?
)