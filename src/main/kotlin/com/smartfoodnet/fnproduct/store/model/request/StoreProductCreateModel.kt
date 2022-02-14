package com.smartfoodnet.fnproduct.store.model.request

import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import io.swagger.annotations.ApiModelProperty

data class StoreProductCreateModel(
    @ApiModelProperty(value = "쇼핑몰 상품 ID")
    var id: Long? = null,
    @ApiModelProperty(value = "쇼핑몰 코드")
    var storeCode: String,
    @ApiModelProperty(value = "쇼핑몰명")
    var storeName: String,
    @ApiModelProperty(value = "고객사 ID")
    var partnerId: Long,
    @ApiModelProperty(value = "쇼핑몰 상품명")
    var name: String,
    @ApiModelProperty(value = "쇼핑몰 상품 코드")
    var storeProductCode: String? = null,
    @ApiModelProperty(value = "옵션명")
    var optionName: String? = null,
    @ApiModelProperty(value = "옵션코드")
    var optionCode: String? = null,
    @ApiModelProperty(value = "쇼핑몰 상품 기본/모음상품 매핑")
    var basicProductMappings: Set<StoreProductBasicProductMappingCreateModel>? = null
) {
    fun toEntity(): StoreProduct {
        return StoreProduct(
            id = id,
            storeCode = storeCode,
            storeName = storeName,
            partnerId = partnerId,
            name = name,
            storeProductCode = storeProductCode,
            optionName = optionName,
            optionCode = optionCode,
        )
    }
}
