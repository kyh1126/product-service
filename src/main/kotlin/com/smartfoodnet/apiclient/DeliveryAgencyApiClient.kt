package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.dto.LotteDeliveryInfoDto
import com.smartfoodnet.apiclient.response.DeliveryInfoModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class DeliveryAgencyApiClient(
    @Value("\${external.service.lotte}") override val host: String,
    override val restTemplate: RestTemplate,
) : RestTemplateClient(host, restTemplate) {

    fun getLotteDeliveryInfo(dto: LotteDeliveryInfoDto): DeliveryInfoModel? {
        return externalPost("/getGdsStatsTracking", dto)
    }
}
