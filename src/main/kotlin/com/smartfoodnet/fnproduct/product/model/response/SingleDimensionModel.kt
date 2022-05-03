package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.product.entity.SingleDimension
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotNull

data class SingleDimensionModel(
    @ApiModelProperty(value = "낱개-가로(mm)")
    var singleWidth: Int? = null,

    @ApiModelProperty(value = "낱개-세로(mm)")
    var singleLength: Int? = null,

    @NotNull
    @ApiModelProperty(value = "낱개-높이(mm)")
    var singleHeight: Int? = null,

    @ApiModelProperty(value = "낱개-무게(g)")
    var singleWeight: Int? = null
) {
    companion object {
        fun fromEntity(singleDimension: SingleDimension): SingleDimensionModel {
            return singleDimension.run {
                SingleDimensionModel(width, length, height, weight)
            }
        }
    }
}
