package com.smartfoodnet.fnproduct.store.model.request

import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import io.swagger.annotations.ApiModelProperty

data class StoreProductCreateModel(
    @ApiModelProperty(value = "쇼핑몰명")
    val storeName: String,
    @ApiModelProperty(value = "쇼핑몰 ID")
    val storeId: Long,
    @ApiModelProperty(value = "쇼핑몰 ID")
    val storeIcon: String,
    @ApiModelProperty(value = "고객사 ID")
    val partnerId: Long,
    @ApiModelProperty(value = "쇼핑몰 상품명")
    val name: String,
    @ApiModelProperty(value = "쇼핑몰 상품 코드")
    val storeProductCode: String,
    @ApiModelProperty(value = "쇼핑몰 상품 옵션")
    val options: List<OptionModel>? = null
) {
    fun toEntity(): StoreProduct {
        return StoreProduct(
            storeId = storeId,
            storeName = storeName,
            storeIcon = storeIcon,
            partnerId = partnerId,
            name = name,
            storeProductCode = storeProductCode
        )
    }
}

data class OptionModel(
    @ApiModelProperty(value = "옵션명")
    val code: String? = null,
    @ApiModelProperty(value = "옵션코드")
    val name: String? = null,
    @ApiModelProperty(value = "쇼핑몰 상품 기본/모음상품 매핑")
    val storeProductBasicProductMappings: List<StoreProductBasicProductMappingCreateModel>? = null
)