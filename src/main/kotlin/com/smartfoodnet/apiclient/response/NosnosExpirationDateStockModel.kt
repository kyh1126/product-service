package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
@ApiModel(value = "노스노스 재고 모델")
data class NosnosExpirationDateStockModel(
    @ApiModelProperty(value = "출고상품 ID", example = "454")
    val shippingProductId: Long? = null,

    @ApiModelProperty(value = "유통기한별 재고 리스트")
    val stocksByExpirationDate: List<StockByExpirationDate>? = null
)

data class StockByExpirationDate(
    val expirationDate: LocalDateTime? = null,
    val normalStock: Int? = null
)
