package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.common.Constants
import com.smartfoodnet.common.Constants.STRING_DATETIME_FORMAT
import com.smartfoodnet.common.parseLocalDateTimeOrNull
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

class LotteDeliveryInfoModel(
    @ApiModelProperty(value = "결과값")
    val rtnList: List<LotteDeliveryInfoDetail> = emptyList(),

    @ApiModelProperty(value = "성공", example = "Y")
    val code: String,

    @ApiModelProperty(value = "실패메세지", example = "")
    val message: String
)

class LotteDeliveryInfoDetail(
    @ApiModelProperty(value = "처리결과코드 (Y:성공)", example = "Y")
    val rtnCd: String,

    @ApiModelProperty(value = "운송장번호", example = "403601798014")
    val invNo: String,

    @ApiModelProperty(value = "화물상태코드", example = "41")
    val gdsStatCd: String,

    @ApiModelProperty(value = "화물상태명", example = "배달완료")
    val gdsStatNm: String,

    @ApiModelProperty(value = "처리일자", example = "20220223")
    val procYmd: String,

    @ApiModelProperty(value = "처리시간", example = "142356")
    val procTme: String,

    @ApiModelProperty(value = "출고반품구분 (01:출고,02:반품)", example = "01")
    val ustRtgSctCd: String,

    @ApiModelProperty(value = "출고반품구분명", example = "출고")
    val ustRtgSctNm: String
) {
    val procDateTime =
        parseLocalDateTimeOrNull(procYmd + procTme, STRING_DATETIME_FORMAT)
}

class CjDeliveryInfoModel(
    val parcelResultMap: ParcelResultMap? = null,
    val parcelDetailResultMap: ParcelDetailResultMap? = null,
)

class ParcelResultMap(val resultList: List<CjDeliveryInfo> = emptyList())
class ParcelDetailResultMap(val resultList: List<CjDeliveryInfoDetail> = emptyList())

class CjDeliveryInfo(
    @ApiModelProperty(value = "운송장번호", example = "555524895224")
    val invcNo: String,

    @ApiModelProperty(value = "보내는이", example = "2**")
    val sendrNm: String,

    @ApiModelProperty(value = "수량", example = "1")
    val qty: Int,

    @ApiModelProperty(value = "상품명", example = "[상온] 우리 참소스 300g [20입]")
    private val itemNm: String,

    @ApiModelProperty(value = "받는이", example = "대**")
    val rcvrNm: String,

    @ApiModelProperty(value = "상태코드", example = "91")
    val nsDlvNm: String
) {
    var deliveryDateTime: LocalDateTime? = null
}

class CjDeliveryInfoDetail(
    @ApiModelProperty(value = "상태코드", example = "91")
    val crgSt: String,

    @ApiModelProperty(value = "처리일시", example = "2022-02-23 11:09:54.0")
    val dTime: String,

    @ApiModelProperty(value = "상태명", example = "배달완료")
    val scanNm: String
) {
    val deliveryDateTime = parseLocalDateTimeOrNull(dTime, Constants.TIMESTAMP1_FORMAT)
}

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
class CjDeliveryStatusModel(
    @JsonProperty("shipping_code")
    val trackingNumber: String,
    val releaseId: Int,
    val releaseStatus: Int,
    val deliveryStatus: String,
)
