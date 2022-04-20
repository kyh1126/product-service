package com.smartfoodnet.fnproduct.store.model.request

import io.swagger.annotations.ApiModelProperty

data class StoreProductUpdateModel(
    @ApiModelProperty(value = "쇼핑몰 상품 ID")
    var id: Long? = null,
    @ApiModelProperty(value = "쇼핑몰 상품명")
    var name: String,
    @ApiModelProperty(value = "옵션코드")
    val optionName: String? = null,
    @ApiModelProperty(value = "쇼핑몰 상품 기본/모음상품 매핑")
    val storeProductBasicProductMappings: List<StoreProductBasicProductMappingCreateModel>
)
