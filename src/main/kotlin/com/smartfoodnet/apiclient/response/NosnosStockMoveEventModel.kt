package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.common.Constants
import io.swagger.annotations.ApiModel
import java.time.LocalDate
import java.time.LocalDateTime

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
@ApiModel(value = "노스노스 재고 모델")
data class NosnosStockMoveEventModel(
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val historyDate: LocalDateTime? = null,
    val shippingProductId: Long? = null,
    val startLocationId: Int? = null,
    val startLocationName: String? = null,
    val endLocationId: Int? = null,
    val endLocationName: String? = null,
    @JsonFormat(pattern = Constants.NOSNOS_DATE_FORMAT)
    val startExpireDate: LocalDate? = null,
    @JsonFormat(pattern = Constants.NOSNOS_DATE_FORMAT)
    val endExpireDate: LocalDate? = null,
    val startLocType: Int? = null,
    val endLocType: Int? = null,
    val moveType: Int? = null,
    val moveQuantity: Int? = null,
    val memo: String? = null,
)