package com.smartfoodnet.fnproduct.store.model.request

import io.swagger.annotations.ApiModelProperty

data class StoreProductBasicProductMappingCreateModel(
    @ApiModelProperty(value = "쇼핑몰 상품 매핑 ID")
    var id: Long? = null,
    @ApiModelProperty(value = "기본 상품 ID")
    var basicProductId: Long? = null,
    @ApiModelProperty(value = "기본상품 개수")
    var quantity: Int = 1
)
