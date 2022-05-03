package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.product.entity.BoxDimension
import io.swagger.annotations.ApiModelProperty

data class BoxDimensionModel(
    @ApiModelProperty(value = "카톤박스-가로(mm)")
    var boxWidth: Int? = null,

    @ApiModelProperty(value = "카톤박스-세로(mm)")
    var boxLength: Int? = null,

    @ApiModelProperty(value = "카톤박스-높이(mm)")
    var boxHeight: Int? = null,

    @ApiModelProperty(value = "카톤박스-무게(g)")
    var boxWeight: Int? = null
) {
    companion object {
        fun fromEntity(boxDimension: BoxDimension): BoxDimensionModel {
            return boxDimension.run {
                BoxDimensionModel(width, length, height, weight)
            }
        }
    }
}
