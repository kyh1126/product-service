package com.smartfoodnet.fnproduct.product.model.request

import com.smartfoodnet.apiclient.response.NosnosShippingProductModel
import com.smartfoodnet.fnproduct.product.entity.BoxDimension
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotNull

class BoxDimensionCreateModel {
    @NotNull
    @ApiModelProperty(value = "카톤박스-가로(mm)")
    var boxWidth: Int? = null

    @NotNull
    @ApiModelProperty(value = "카톤박스-세로(mm)")
    var boxLength: Int? = null

    @NotNull
    @ApiModelProperty(value = "카톤박스-높이(mm)")
    var boxHeight: Int? = null

    @ApiModelProperty(value = "카톤박스-무게(g)")
    var boxWeight: Int? = null

    fun toEntity(): BoxDimension {
        return BoxDimension(width = boxWidth!!, length = boxLength!!, height = boxHeight!!, weight = boxWeight)
    }

    companion object {
        fun fromModel(model: NosnosShippingProductModel): BoxDimensionCreateModel {
            return BoxDimensionCreateModel().also {
                it.boxWidth = model.boxWidth
                it.boxLength = model.boxLength
                it.boxHeight = model.boxHeight
                it.boxWeight = model.boxWeight
            }
        }
    }
}
