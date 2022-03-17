package com.smartfoodnet.fninventory.stock.support

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import com.smartfoodnet.fninventory.stock.support.BasicProductStockSearchCondition.SearchType.*
import io.swagger.annotations.ApiModelProperty

class BasicProductStockSearchCondition(
    @ApiModelProperty(hidden = true)
    var partnerId: Long? = null,
/*
    @ApiModelProperty(value = "기본상품명")
    var basicProductName: String? = null,

    @ApiModelProperty(value = "기본상품코드")
    var basicProductCode: String? = null,

    @ApiModelProperty(value = "상품바코드")
    var barcode: String? = null,
*/
    @ApiModelProperty(value = "유통기한관리여부")
    var expirationDateManagementYn: String? = null,
) : PredicateSearchCondition() {
    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        return predicate.orAllOf(
            eqPartnerId(partnerId),
            /*
            likeBasicProductName(basicProductName),
            eqBasicProductCode(basicProductCode),
            eqBarcode(barcode),
             */
            searchType?.let { toPredicate(it) },
            eqExpirationDateManagementYn(expirationDateManagementYn)
        )
    }

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

    private fun toPredicate(searchType: SearchType) =
        when (searchType) {
            NAME -> containBasicProductName(searchKeyword)
            CODE -> containBasicProductCode(searchKeyword)
            BARCODE -> containBarcode(searchKeyword)
        }

    private fun eqPartnerId(partnerId: Long?) = partnerId?.let { basicProduct.partnerId.eq(it) }

    private fun containBasicProductName(basicProductName: String?) =
        basicProductName?.let { basicProduct.name.contains(it) }

    private fun containBasicProductCode(basicProductCode: String?) =
        basicProductCode?.let { basicProduct.code.contains(it) }

    private fun containBarcode(barcode: String?) =
        barcode?.let { basicProduct.barcode.contains(it) }

    private fun eqExpirationDateManagementYn(expirationDateManagementYn: String?) =
        expirationDateManagementYn?.let { basicProduct.expirationDateManagementYn.eq(it) }
}