package com.smartfoodnet.fnproduct.product.model.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import io.swagger.annotations.ApiModelProperty

class PackageProductSearchCondition(
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    var partnerId: Long? = null,

    @ApiModelProperty(hidden = true)
    @JsonIgnore
    var type: BasicProductType? = null,

    @ApiModelProperty(value = "상품명")
    var name: String? = null,

    @ApiModelProperty(value = "상품코드")
    val code: String? = null,

    @ApiModelProperty(value = "활성화여부")
    var activeYn: String? = null,
) : PredicateSearchCondition() {

    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        return predicate.orAllOf(
            eqPartnerId(partnerId),
            eqType(type),
            eqName(name),
            eqCode(code),
            eqActiveYn(activeYn),
        )
    }

    private fun eqPartnerId(partnerId: Long?) = partnerId?.let { basicProduct.partnerId.eq(it) }

    private fun eqType(type: BasicProductType?) = type?.let { basicProduct.type.eq(it) }

    private fun eqActiveYn(activeYn: String?) = activeYn?.let { basicProduct.activeYn.eq(it) }

    private fun eqName(name: String?) = name?.let { basicProduct.name.eq(it) }

    private fun eqCode(code: String?) = code?.let { basicProduct.code.eq(it) }
}
