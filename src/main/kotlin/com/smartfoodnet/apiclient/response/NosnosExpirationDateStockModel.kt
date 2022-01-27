package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.common.Constants
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDate

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
@ApiModel(value = "노스노스 재고 모델")
data class NosnosExpirationDateStockModel(
    @ApiModelProperty(value = "출고상품 ID", example = "454")
    val shippingProductId: Long? = null,

    @ApiModelProperty(value = "유통기한")
    @JsonFormat(pattern = Constants.NOSNOS_DATE_FORMAT)
    @JsonProperty("expire_date")
    val expirationDate: LocalDate? = null,

    @ApiModelProperty(value = "가용재고")
    val normalStock: Int? = null,

    @ApiModelProperty(value = "총재고")
    val totalStock: Int? = null
)
