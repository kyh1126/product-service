package com.smartfoodnet.fninventory.shortage.support

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fninventory.shortage.support.ProductShortageSearchCondition.SearchType.*
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import io.swagger.annotations.ApiModelProperty

class ProductShortageSearchCondition(
    @ApiModelProperty(hidden = true)
    var partnerId: Long? = null,


    ):PredicateSearchCondition() {

    @ApiModelProperty(
        value = "상품별검색 (NAME:기본상품명/CODE:기본상품코드/BARCODE:상품바코드)",
        allowableValues = "NAME,CODE,BARCODE"
    )
    var searchType: SearchType? = null

    @ApiModelProperty(value = "검색 키워드")
    var searchKeyword: String? = null

    enum class SearchType {
        NAME, CODE, BARCODE
    }

    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        return predicate.orAllOf(
            searchType?.let { toPredicate(it) }

        )
    }

    private fun toPredicate(searchType: SearchType) =
        when(searchType){
            NAME -> containBasicProductName(searchKeyword)
            CODE -> containBasicProductCode(searchKeyword)
            BARCODE -> containBarcode(searchKeyword)

        }

    private fun containBasicProductName(basicProductName: String?)=
        basicProductName?.let { basicProduct.name.contains(it) }

    private fun containBasicProductCode(basicProductCode: String?) =
        basicProductCode?.let { basicProduct.code.contains(it) }

    private fun containBarcode(barcode: String?) =
        barcode?.let { basicProduct.barcode.contains(it) }

}