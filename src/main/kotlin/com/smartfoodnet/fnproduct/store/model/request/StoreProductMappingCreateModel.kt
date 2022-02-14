package com.smartfoodnet.fnproduct.store.model.request

import io.swagger.annotations.ApiModelProperty

data class StoreProductMappingCreateModel(
    @ApiModelProperty(value = "쇼핑몰 상품 ID")
    var storeProductId: Long,
    @ApiModelProperty(value = "쇼핑몰 상품 기본 상품 매핑")
    var mappings: Set<StoreProductBasicProductMappingCreateModel>

)
