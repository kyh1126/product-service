package com.smartfoodnet.fninventory.stock.support

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import io.swagger.annotations.ApiModelProperty

class BasicProductStockSearchCondition(
    @ApiModelProperty(hidden = true)
    var partnerId: Long? = null,

    @ApiModelProperty(value = "기본상품명")
    var basicProductName: String? = null,

    @ApiModelProperty(value = "기본상품코드")
    var basicProductCode: String? = null,

    @ApiModelProperty(value = "상품바코드")
    var barcode: String? = null,

    @ApiModelProperty(value = "유통기한관리여부")
    var expirationDateManagementYn: String? = null,
) : PredicateSearchCondition() {
    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        return predicate.orAllOf(
            eqPartnerId(partnerId),
            likeBasicProductName(basicProductName),
            eqBasicProductCode(basicProductCode),
            eqBarcode(barcode),
            eqExpirationDateManagementYn(expirationDateManagementYn)
        )
    }

    private fun eqPartnerId(partnerId: Long?) = partnerId?.let { basicProduct.partnerId.eq(it) }

    private fun likeBasicProductName(basicProductName: String?) =
        basicProductName?.let { basicProduct.name.likeIgnoreCase("%$it%") }

    private fun eqBasicProductCode(basicProductCode: String?) =
        basicProductCode?.let { basicProduct.code.eq(it) }

    private fun eqBarcode(barcode: String?) =
        barcode?.let { basicProduct.barcode.eq(it) }

    private fun eqExpirationDateManagementYn(expirationDateManagementYn: String?) =
        expirationDateManagementYn?.let { basicProduct.expirationDateManagementYn.eq(it) }
}