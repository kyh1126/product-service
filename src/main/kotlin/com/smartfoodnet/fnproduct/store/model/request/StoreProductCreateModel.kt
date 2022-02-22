package com.smartfoodnet.fnproduct.store.model.request

import io.swagger.annotations.ApiModelProperty

data class StoreProductCreateModel(
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
    @ApiModelProperty(value = "쇼핑몰 상품 옵션")
    val options: List<OptionModel>
)

data class OptionModel(
    @ApiModelProperty(value = "옵션명")
    val code: String? = null,
    @ApiModelProperty(value = "옵션코드")
    val name: String? = null,
    @ApiModelProperty(value = "쇼핑몰 상품 기본/모음상품 매핑")
    val storeProductBasicProductMappings: List<StoreProductBasicProductMappingCreateModel>? = null
)