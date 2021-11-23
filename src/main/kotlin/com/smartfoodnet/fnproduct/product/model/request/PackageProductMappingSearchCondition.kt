package com.smartfoodnet.fnproduct.product.model.request

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModelProperty

class PackageProductMappingSearchCondition(
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    var partnerId: Long? = null,

    @ApiModelProperty(value = "활성화여부(모음상품)", allowableValues = "Y,N")
    var activeYn: String? = null,
) {

    enum class SearchType {
        PACKAGE_NAME, PACKAGE_CODE, NAME, CODE, BARCODE
    }

    @ApiModelProperty(
        value = "상품별검색 (PACKAGE_NAME:모음상품명/PACKAGE_CODE:모음상품코드/NAME:기본상품명/CODE:기본상품코드/BARCODE:기본상품바코드)",
        allowableValues = "PACKAGE_NAME,PACKAGE_CODE,NAME,CODE,BARCODE"
    )
    var searchType: SearchType? = null

    @ApiModelProperty(value = "검색 키워드")
    var searchKeyword: String? = null
}
