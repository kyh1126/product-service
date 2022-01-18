package com.smartfoodnet.apiclient.response
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NosnosDailyStockSummaryModel(
    val shippingProductId: Long,
    val stockDate: String? = null,
    val receivingQuantity: Int? = null,
    val shipoutQuantity: Int? = null,
    val returnQuantity: Int? = null,
    val outQuantity: Int? = null,
    val returnBackQuantity: Int? = null,
    val returnReceiveQuantity: Int? = null,
    val rollbackReceiveQuantity: Int? = null,
    val adjustInQuantity: Int? = null,
    val adjustOutQuantity: Int? = null
)