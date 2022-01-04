package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.swagger.annotations.ApiModelProperty

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CommonDataListModel<T>(
    val dataList: List<T>,
    @ApiModelProperty(value = "전체갯수", example = "2")
    val totalCount: Long,
    @ApiModelProperty(value = "전체페이지", example = "1")
    val totalPage: Long,
    @ApiModelProperty(value = "현재페이지", example = "1")
    val currentPage: Long
)