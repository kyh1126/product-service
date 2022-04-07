package com.smartfoodnet.apiclient.response

import com.smartfoodnet.common.Constants.STRING_DATETIME_FORMAT
import com.smartfoodnet.common.parseLocalDateTimeOrNull
import io.swagger.annotations.ApiModelProperty

class DeliveryInfoModel(
    @ApiModelProperty(value = "결과값")
    val rtnList: List<DeliveryInfoDetail> = emptyList(),

    @ApiModelProperty(value = "성공", example = "Y")
    val code: String,

    @ApiModelProperty(value = "실패메세지", example = "")
    val message: String
)

class DeliveryInfoDetail(
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
        parseLocalDateTimeOrNull(this.procYmd + this.procTme, STRING_DATETIME_FORMAT)
}
