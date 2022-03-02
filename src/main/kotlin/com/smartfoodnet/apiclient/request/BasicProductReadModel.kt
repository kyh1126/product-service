package com.smartfoodnet.apiclient.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.swagger.annotations.ApiModelProperty

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class BasicProductReadModel(
        @ApiModelProperty(value = "파트너 ID", example = "92")
        var partnerId: Long? = null,
        @ApiModelProperty(value = "고객사 ID", example = "77")
        var memberId: Long? = null,
        @ApiModelProperty(value = "상품코드")
        var productCode: String? = null,
        @ApiModelProperty(value = "상품명")
        var productName: String? = null,
        @ApiModelProperty(value = "출고상품 구분 ID")
        var categoryId: Long? = null,
        @ApiModelProperty(value = "활성화 여부", example = "1")
        var status: Int? = null,
        @ApiModelProperty(value = "페이지 번호", example = "1")
        var page: Int? = null,
)
