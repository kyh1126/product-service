package com.smartfoodnet.fnproduct.product.model.request

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.PackageProductMapping
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotNull

data class PackageProductMappingCreateModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "모음상품 ID")
    val packageProductId: Long?,

    @NotNull
    @ApiModelProperty(value = "기본상품정보")
    val basicProductModel: BasicProductSimpleCreateModel,

    @NotNull
    @ApiModelProperty(value = "수량")
    val quantity: Int,
) {
    fun toEntity(basicProduct: BasicProduct): PackageProductMapping {
        return PackageProductMapping(
            selectedBasicProduct = basicProduct,
            quantity = quantity,
        )
    }
}
