package com.smartfoodnet.apiclient

import com.fasterxml.jackson.databind.ObjectMapper
import com.smartfoodnet.apiclient.request.LotteDeliveryInfoDto
import com.smartfoodnet.apiclient.request.LotteTrackingDto
import com.smartfoodnet.fnproduct.release.model.vo.DeliveryStatus
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test

internal class DeliveryInfoApiClientTest {
    val lotteHost = "https://ftr.alps.llogis.com:18260/openapi/ftr"
    val cjHost = "https://www.cjlogistics.com/ko/tool"

    @Test
    fun lotte() {
        val objectMapper = ObjectMapper()
        val dto = LotteDeliveryInfoDto(sendParamList = listOf(
            LotteTrackingDto("403601798014", DeliveryStatus.COMPLETED_LOTTE.code)
        ))

        val dtoJsonString = objectMapper.writeValueAsString(dto)
        val document = Jsoup.connect("$lotteHost/getGdsStatsTracking")
            .requestBody(dtoJsonString)
            .ignoreContentType(true)
            .header("Content-Type", "application/json")
            .post().body().text()

        print(document)
    }

    @Test
    fun cjLogistics() {
        Jsoup.connect("$cjHost/parcel/tracking").execute().run {
            val doc = parse()
            val cookies = cookies()
            val csrf = doc.select("input[name=_csrf]").first()?.attr("value")
                ?: throw IllegalStateException("_csrf 값을 찾을 수 없습니다.")

            val result = Jsoup.connect("$cjHost/parcel/tracking-detail")
                .ignoreContentType(true)
                .cookies(cookies)
                .data("paramInvcNo", "555524895224")
                .data("_csrf", csrf)
                .post().body().text()

            println(result)
        }
    }
}
