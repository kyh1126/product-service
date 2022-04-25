package com.smartfoodnet.fnproduct.product.model.request

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.BoxDimension
import com.smartfoodnet.fnproduct.product.entity.ExpirationDateInfo
import com.smartfoodnet.fnproduct.product.entity.SingleDimension
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class BasicProductPackageCreateModel {
    @ApiModelProperty(value = "id")
    val id: Long? = null

    @field:NotNull
    @ApiModelProperty(value = "화주(고객사) ID")
    val partnerId: Long? = null

    @field:NotEmpty
    @ApiModelProperty(value = "화주(고객사) 코드")
    val partnerCode: String? = null

    @field:NotEmpty
    @ApiModelProperty(value = "상품명")
    val name: String? = null

    @ApiModelProperty(value = "활성화여부 (default: Y)", allowableValues = "Y,N")
    val activeYn: String = "Y"

    fun toEntity(code: String): BasicProduct {
        return BasicProduct(
            type = BasicProductType.PACKAGE,
            partnerId = partnerId,
            name = name,
            code = code,
            handlingTemperature = HandlingTemperatureType.MIX,
            expirationDateInfo = ExpirationDateInfo.default,
            activeYn = activeYn,
            singleDimension = SingleDimension.default,
            boxDimension = BoxDimension.default
        )
    }
}
