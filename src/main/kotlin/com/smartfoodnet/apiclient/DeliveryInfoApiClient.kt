package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.request.LotteDeliveryInfoDto
import com.smartfoodnet.apiclient.response.CjDeliveryInfo
import com.smartfoodnet.apiclient.response.CjDeliveryInfoModel
import com.smartfoodnet.apiclient.response.LotteDeliveryInfoModel
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

@Component
class LotteDeliveryInfoApiClient(
    @Value("\${external.service.lotte}") override val host: String,
    override val restTemplate: RestTemplate,
) : RestTemplateClient(host, restTemplate) {

    fun getDeliveryInfo(request: LotteDeliveryInfoDto): LotteDeliveryInfoModel? {
        return externalPost<LotteDeliveryInfoModel>("/getGdsStatsTracking", request).body
    }
}

@Component
class CjDeliveryInfoApiClient(
    @Value("\${external.service.cj}") override val host: String,
    override val restTemplate: RestTemplate,
) : RestTemplateClient(host, restTemplate) {

    fun getDeliveryInfo(trackingNumbers: List<String>): List<CjDeliveryInfo> {
        val responseEntity = externalGet<String>("/parcel/tracking")
        val csrf = getCSRF(responseEntity.body!!)
        val cookies = responseEntity.headers[HttpHeaders.SET_COOKIE] ?: emptyList()

        return trackingNumbers.mapNotNull { getDeliveryInfo(it, csrf, cookies) }
    }

    private fun getDeliveryInfo(
        trackingNumber: String,
        csrf: String,
        cookies: List<String>
    ): CjDeliveryInfo? {
        if (csrf.isEmpty() || trackingNumber.isEmpty() || cookies.isEmpty()) return null

        val body = LinkedMultiValueMap<String, String>().apply {
            add("_csrf", csrf)
            add("paramInvcNo", trackingNumber)
        }
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            addAll(HttpHeaders.COOKIE, cookies)
        }
        val response =
            externalPost<CjDeliveryInfoModel>("/parcel/tracking-detail", body, headers).body

        val deliveryDateTimeByTrackingNumber = response?.parcelDetailResultMap?.resultList
            ?.associateBy({ it.crgSt }, { it.deliveryDateTime }) ?: emptyMap()

        return response?.parcelResultMap?.resultList?.firstOrNull()?.apply {
            deliveryDateTime = deliveryDateTimeByTrackingNumber[nsDlvNm]
        }
    }

    private fun getCSRF(body: String): String {
        val elements = Jsoup.parse(body).getElementsByAttributeValue("name", "_csrf")
        var csrf = ""
        if (elements.isNotEmpty()) {
            csrf = elements[0].attr("value")
        }
        return csrf
    }
}
