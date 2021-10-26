package com.smartfoodnet.fnproduct.product.model.request

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.PackageProduct
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotNull

data class PackageProductCreateModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "기본상품 ID")
    val basicProductId: Long?,

    @NotNull
    @ApiModelProperty(value = "모음상품정보")
    val packageProduct: BasicProductSimpleCreateModel,

    @NotNull
    @ApiModelProperty(value = "수량")
    val quantity: Int,
) {
    fun toEntity(packageProduct: BasicProduct): PackageProduct {
        return PackageProduct(
            packageProduct = packageProduct,
            quantity = quantity,
        )
    }
}
