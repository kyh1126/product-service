package com.smartfoodnet.apiclient.request

import io.swagger.annotations.ApiModelProperty

class LotteDeliveryInfoDto(
    val sendParamList: List<LotteTrackingDto> = emptyList()
)

class LotteTrackingDto(
    @ApiModelProperty(value = "롯데택배운송장번호", example = "403601798014")
    var invNo: String,

    @ApiModelProperty(value = "화물상태코드(쉼표로구분)", example = "41", notes = "DeliveryStatus 참고")
    var gdsStatCds: String
)
