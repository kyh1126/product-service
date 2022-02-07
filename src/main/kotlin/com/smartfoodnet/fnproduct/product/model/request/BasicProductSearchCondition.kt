package com.smartfoodnet.fnproduct.product.model.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import com.smartfoodnet.fnproduct.product.model.request.BasicProductSearchCondition.SearchType.*
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import io.swagger.annotations.ApiModelProperty

class BasicProductSearchCondition(
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    var partnerId: Long? = null,

    @ApiModelProperty(value = "입고처 ID", example = "1")
    var warehouseId: Long? = null,

    @ApiModelProperty(value = "유통기한관리여부", allowableValues = "Y,N")
    var expirationDateManagementYn: String? = null,

    @ApiModelProperty(value = "활성화여부", allowableValues = "Y,N")
    var activeYn: String? = null,
) : PredicateSearchCondition() {
    private val basicProductApiTypes = setOf(BasicProductType.BASIC, BasicProductType.CUSTOM_SUB)

    enum class SearchType {
        NAME, CODE, BARCODE
    }

    @ApiModelProperty(
        value = "구분 (BASIC:기본상품/CUSTOM_SUB:고객전용부자재) ex> BASIC,CUSTOM_SUB",
        allowableValues = "BASIC,CUSTOM_SUB",
    )
    var types: Set<BasicProductType> = basicProductApiTypes
        get() = field.ifEmpty { basicProductApiTypes }

    @ApiModelProperty(
        value = "상품별검색 (NAME:기본상품명/CODE:기본상품코드/BARCODE:상품바코드)",
        allowableValues = "NAME,CODE,BARCODE"
    )
    var searchType: SearchType? = null

    @ApiModelProperty(value = "검색 키워드")
    var searchKeyword: String? = null

    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        return predicate.orAllOf(
            eqPartnerId(partnerId),
            inType(types),
            eqWarehouse(warehouseId),
            eqExpirationDateManagementYn(expirationDateManagementYn),
            eqActiveYn(activeYn),
            searchType?.let { toPredicate(it) }
        )
    }

    private fun toPredicate(searchType: SearchType) =
        when (searchType) {
            NAME -> containsName(searchKeyword)
            CODE -> containsCode(searchKeyword)
            BARCODE -> containsBarcode(searchKeyword)
        }

    private fun eqPartnerId(partnerId: Long?) = partnerId?.let { basicProduct.partnerId.eq(it) }

    private fun inType(types: Collection<BasicProductType>) = basicProduct.type.`in`(types)

    private fun eqWarehouse(warehouseId: Long?) =
        warehouseId?.let { basicProduct.warehouse.id.eq(it) }

    private fun eqExpirationDateManagementYn(expirationDateManagementYn: String?) =
        expirationDateManagementYn?.let { basicProduct.expirationDateManagementYn.eq(it) }

    private fun eqActiveYn(activeYn: String?) = activeYn?.let { basicProduct.activeYn.eq(it) }

    private fun containsName(name: String?) = name?.let { basicProduct.name.contains(it) }

    private fun containsCode(code: String?) = code?.let { basicProduct.code.contains(it) }

    private fun containsBarcode(barcode: String?) =
        barcode?.let { basicProduct.barcode.contains(it) }
}
