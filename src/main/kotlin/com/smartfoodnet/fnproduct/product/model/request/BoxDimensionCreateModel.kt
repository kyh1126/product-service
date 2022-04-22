package com.smartfoodnet.fnproduct.product.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.smartfoodnet.fnproduct.product.entity.BoxDimension
import io.swagger.annotations.ApiModelProperty

data class BoxDimensionCreateModel(
    @JsonProperty("boxWidth")
    @ApiModelProperty(value = "카톤박스-가로(mm)")
    var width: Int? = null,

    @JsonProperty("boxLength")
    @ApiModelProperty(value = "카톤박스-세로(mm)")
    var length: Int? = null,

    @JsonProperty("boxHeight")
    @ApiModelProperty(value = "카톤박스-높이(mm)")
    var height: Int? = null,

    @JsonProperty("boxWeight")
    @ApiModelProperty(value = "카톤박스-무게(g)")
    var weight: Int? = null
) {
    fun toEntity(): BoxDimension {
        return BoxDimension(width = width, length = length, height = height, weight = weight)
    }
}
