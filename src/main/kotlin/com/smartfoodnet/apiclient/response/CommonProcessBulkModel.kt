package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.annotation.JsonProperty

data class CommonProcessBulkModel<T>(
    @JsonProperty("processed_data_list")
    val processedDataList: List<T>,
    @JsonProperty("processed_count")
    val processedCount: Int
)
