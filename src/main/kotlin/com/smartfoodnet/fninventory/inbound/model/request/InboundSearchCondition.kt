package com.smartfoodnet.fninventory.inbound.model.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.Constants
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fninventory.inbound.entity.QInbound
import com.smartfoodnet.fninventory.inbound.entity.QInbound.inbound
import com.smartfoodnet.fninventory.inbound.entity.QInboundExpectedDetail
import com.smartfoodnet.fninventory.inbound.entity.QInboundUnplanned.inboundUnplanned
import com.smartfoodnet.fninventory.inbound.model.vo.InboundStatusAdvanceType
import com.smartfoodnet.fninventory.inbound.model.vo.InboundStatusType
import com.smartfoodnet.fninventory.inbound.model.vo.ProductSearchType
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class InboundSearchCondition(
    @ApiModelProperty(value = "고객사 ID", example = "1", hidden = true)
    @JsonIgnore
    var partnerId: Long? = null,

    @ApiModelProperty(value = "검색 시작 일자", example = "2021-12-01 00:00:00")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val fromDate: LocalDateTime,

    @ApiModelProperty(value = "검색 종료 일자", example = "2021-12-31 23:59:59")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val toDate: LocalDateTime,

    @ApiModelProperty(value = "입고상태", example = "EXPECTED")
    val statusType: InboundStatusType? = null,

    @ApiModelProperty(value = "상품별검색")
    val productSearchType: ProductSearchType? = null,

    @ApiModelProperty(value = "상품별검색")
    val keyword: String? = null
): PredicateSearchCondition(){
    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        predicate.and(inbound.partnerId.eq(partnerId))
        predicate.and(inbound.createdAt.between(fromDate, toDate))

        if (statusType != null){
            predicate.and(inbound.status.eq(statusType))
        }

        if (!keyword.isNullOrBlank()) {
            when (productSearchType) {
                ProductSearchType.BASIC_PRODUCT_CODE -> predicate.and(QInboundExpectedDetail.inboundExpectedDetail.basicProduct.code.contains(keyword))
                ProductSearchType.BASIC_PRODUCT_NAME -> predicate.and(QInboundExpectedDetail.inboundExpectedDetail.basicProduct.name.contains(keyword))
                ProductSearchType.INBOUND_REGISTRATION_NO -> predicate.and(inbound.registrationNo.contains(keyword))
            }
        }

        return predicate
    }
}