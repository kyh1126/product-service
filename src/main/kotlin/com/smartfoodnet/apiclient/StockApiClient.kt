package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.response.GetInboundModel
import com.smartfoodnet.fninventory.inbound.model.request.InboundCreateModel
import org.springframework.stereotype.Component

@Component
class InboundApiClient : RestTemplateClient() {
    fun getInbound(receivingPlanId : Long) : GetInboundModel?{
        return get("/inventory/${receivingPlanId}")
    }
}