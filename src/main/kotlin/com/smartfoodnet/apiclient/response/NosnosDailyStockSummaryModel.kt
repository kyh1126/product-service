package com.smartfoodnet.apiclient.response
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NosnosDailyStockSummaryModel(
    val shippingProductId: Long? = null,
    val stockDate: String? = null,
    val receivingQuantity: Int = 0,
    val shipoutQuantity: Int = 0,
    val returnQuantity: Int = 0,
    val outQuantity: Int = 0,
    val returnBackQuantity: Int = 0,
    val returnReceiveQuantity: Int = 0,
    val rollbackReceiveQuantity: Int = 0,
    val adjustInQuantity: Int = 0,
    val adjustOutQuantity: Int = 0
)