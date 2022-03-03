package com.smartfoodnet.fnproduct.store.model.request

import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import io.swagger.annotations.ApiModelProperty

data class StoreProductBulkCreateModel(
    @ApiModelProperty(value = "쇼핑몰명")
    val storeName: String,
    @ApiModelProperty(value = "쇼핑몰 ID")
    val storeId: Long,
    @ApiModelProperty(value = "쇼핑몰 아이콘")
    val storeIcon: String,
    @ApiModelProperty(value = "고객사 ID")
    val partnerId: Long,
    @ApiModelProperty(value = "쇼핑몰 상품명")
    val name: String,
    @ApiModelProperty(value = "쇼핑몰 상품 코드")
    val storeProductCode: String,
    @ApiModelProperty(value = "옵션명")
    val optionCode: String? = null,
    @ApiModelProperty(value = "옵션코드")
    val optionName: String? = null,
    @ApiModelProperty(value = "기본상품코드")
    val basicProductCode: String? = null,
    @ApiModelProperty(value = "기본상품명")
    val basicProductName: String? = null,
    @ApiModelProperty(value = "기본상품 개수")
    val basicProductQuantity: Int? = null,
) {
    fun toEntity(): StoreProduct {
        return StoreProduct(
            storeName = storeName,
            storeId = storeId,
            storeIcon = storeIcon,
            partnerId = partnerId,
            name = name,
            storeProductCode = storeProductCode,
            optionCode = optionCode,
            optionName = optionName
        )
    }
}