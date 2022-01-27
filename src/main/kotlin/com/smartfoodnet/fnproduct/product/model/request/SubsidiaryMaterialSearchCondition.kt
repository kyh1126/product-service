package com.smartfoodnet.fnproduct.product.model.request

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import com.smartfoodnet.fnproduct.product.model.request.SubsidiaryMaterialSearchCondition.SubsidiaryMaterialType.Companion.subsidiaryMaterialTypes
import com.smartfoodnet.fnproduct.product.model.request.SubsidiaryMaterialSearchCondition.SubsidiaryMaterialType.Companion.toBasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import io.swagger.annotations.ApiModelProperty

class SubsidiaryMaterialSearchCondition : PredicateSearchCondition() {
    @ApiModelProperty(value = "화주(고객사) ID")
    var partnerId: Long? = null

    @ApiModelProperty(
        value = "구분 (SUB:공통부자재/CUSTOM_SUB:고객전용부자재) ex> SUB,CUSTOM_SUB",
        allowableValues = "SUB,CUSTOM_SUB",
    )
    var types: Set<SubsidiaryMaterialType> = subsidiaryMaterialTypes
        get() = field.ifEmpty { subsidiaryMaterialTypes }

    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        if (BasicProductType.SUB in toBasicProductType(types)) {
            predicate.and(forSub())
        }
        if (BasicProductType.CUSTOM_SUB in toBasicProductType(types)) {
            predicate.or(forCustomSub())
        }
        return predicate
    }

    private fun forSub() = eqType(BasicProductType.SUB)

    private fun forCustomSub() = eqType(BasicProductType.CUSTOM_SUB)
        .and(eqPartnerId(partnerId) ?: isNullPartnerId)

    private fun eqType(type: BasicProductType) = basicProduct.type.eq(type)

    private fun eqPartnerId(partnerId: Long?) = partnerId?.let { basicProduct.partnerId.eq(it) }

    private val isNullPartnerId = basicProduct.partnerId.isNull

    enum class SubsidiaryMaterialType(val basicProductType: BasicProductType) {
        SUB(BasicProductType.SUB),
        CUSTOM_SUB(BasicProductType.CUSTOM_SUB);

        companion object {
            val subsidiaryMaterialTypes = values().toSet()

            fun toBasicProductType(types: Collection<SubsidiaryMaterialType>) =
                types.map { it.basicProductType }
        }
    }

    companion object {
        fun subCondition(): SubsidiaryMaterialSearchCondition {
            val condition = SubsidiaryMaterialSearchCondition()
            condition.types = setOf(SubsidiaryMaterialType.SUB)
            return condition
        }
    }
}
