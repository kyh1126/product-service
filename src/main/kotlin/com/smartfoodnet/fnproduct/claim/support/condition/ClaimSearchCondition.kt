package com.smartfoodnet.fnproduct.claim.support.condition

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.Constants
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.claim.entity.QClaim.claim
import com.smartfoodnet.fnproduct.claim.model.vo.ExchangeStatus
import com.smartfoodnet.fnproduct.claim.model.vo.ReturnStatus
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDate

class ClaimSearchCondition(
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    var partnerId: Long? = null,

    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    @ApiModelProperty(value = "반품접수일")
    val claimedAt: LocalDate? = null,

    @ApiModelProperty(value = "원송장번호")
    val originalTrackingNumber: String? = null,

    @ApiModelProperty(value = "교환송장번호")
    val exchangeTrackingNumber: String? = null,

    @ApiModelProperty(value = "주문자이름")
    val customerName: String? = null,

    @ApiModelProperty(value = "반품상태 리스트")
    val returnStates: List<ReturnStatus>? = null,

    @ApiModelProperty(value = "교환출고상태 리스트")
    val exchangeStates: List<ExchangeStatus>? = null

) : PredicateSearchCondition() {
    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        return predicate.orAllOf(
            partnerId?.let { claim.partnerId.eq(partnerId) },
            originalTrackingNumber?.let { claim.releaseInfo.trackingNumber.eq(it) },
            exchangeTrackingNumber?.let { claim.exchangeRelease.trackingNumber.eq(it) },
            customerName?.let { claim.customerName.eq(it) },
            claimedAt?.let {
                claim.claimedAt.between(
                    it.atStartOfDay(),
                    it.plusDays(1).atStartOfDay()
                )
            },
            returnStates?.let{ claim.returnStatus.`in`(it) },
            exchangeStates?.let{ claim.exchangeStatus.`in`(it) }
        )
    }
}
