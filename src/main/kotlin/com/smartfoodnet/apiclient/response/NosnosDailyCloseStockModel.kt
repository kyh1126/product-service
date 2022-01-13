package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.common.Constants
import io.swagger.annotations.ApiModel
import java.time.LocalDate

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
@ApiModel(value = "노스노스 일별 마감 재고 모델")
data class NosnosDailyCloseStockModel(
    val shippingProductId: Long? = null,
    @JsonFormat(pattern = Constants.NOSNOS_DATE_FORMAT)
    val closingDate: LocalDate? = null,
    val totalQuantity: Int? = null,
    val receivingStock: Int? = null,
    val normalStock: Int? = null,
    val orderStock: Int? = null,
    val shippingStock: Int? = null,
    val damagedStock: Int? = null,
    val returnStock: Int? = null
)