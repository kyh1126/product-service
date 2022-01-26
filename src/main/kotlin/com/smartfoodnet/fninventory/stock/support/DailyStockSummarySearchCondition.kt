package com.smartfoodnet.fninventory.stock.support

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.Constants.STARTING_FROM
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fninventory.stock.entity.QDailyStockSummary.dailyStockSummary
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDate

class DailyStockSummarySearchCondition(
    @ApiModelProperty(value = "고객 ID")
    var partnerId: Long? = null,

    @ApiModelProperty(value = "기본상품 ID")
    var basicProductId: Long? = null
) : PredicateSearchCondition() {
    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        return predicate.orAllOf(
            eqPartnerId(partnerId),
            eqBasicProductId(basicProductId),
            gteStartDate()
        )
    }

    private fun eqPartnerId(partnerId: Long?) = partnerId?.let { dailyStockSummary.partnerId.eq(it) }

    private fun eqBasicProductId(basicProductId: Long?) =
        basicProductId?.let { dailyStockSummary.basicProduct.id.eq(basicProductId) }

    private fun gteStartDate() = dailyStockSummary.effectiveDate.gt(LocalDate.now().minusDays(STARTING_FROM))
}