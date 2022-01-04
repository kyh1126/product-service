package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.response.GetInboundModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class InboundApiClient(
    @Value("\${sfn.service.fn-nosnos}") override val host: String,
    override val restTemplate: RestTemplate
) : RestTemplateClient(host, restTemplate) {

}
