package com.smartfoodnet.fnproduct.product.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.smartfoodnet.fnproduct.product.entity.SingleDimension
import io.swagger.annotations.ApiModelProperty

data class SingleDimensionCreateModel(
    @JsonProperty("singleWidth")
    @ApiModelProperty(value = "낱개-가로(mm)")
    var width: Int? = null,

    @JsonProperty("singleLength")
    @ApiModelProperty(value = "낱개-세로(mm)")
    var length: Int? = null,

    @JsonProperty("singleHeight")
    @ApiModelProperty(value = "낱개-높이(mm)")
    var height: Int? = null,

    @JsonProperty("singleWeight")
    @ApiModelProperty(value = "낱개-무게(g)")
    var weight: Int? = null
) {
    fun toEntity(): SingleDimension {
        return SingleDimension(width = width, length = length, height = height, weight = weight)
    }
}
