package com.smartfoodnet.fnproduct.product.model.request

import com.smartfoodnet.fnproduct.product.entity.SingleDimension
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotNull

class SingleDimensionCreateModel {
    @NotNull
    @ApiModelProperty(value = "낱개-가로(mm)")
    var singleWidth: Int? = null

    @NotNull
    @ApiModelProperty(value = "낱개-세로(mm)")
    var singleLength: Int? = null

    @NotNull
    @ApiModelProperty(value = "낱개-높이(mm)")
    var singleHeight: Int? = null

    @ApiModelProperty(value = "낱개-무게(g)")
    var singleWeight: Int? = null

    fun toEntity(): SingleDimension {
        return SingleDimension(width = singleWidth!!, length = singleLength!!, height = singleHeight!!, weight = singleWeight)
    }
}
