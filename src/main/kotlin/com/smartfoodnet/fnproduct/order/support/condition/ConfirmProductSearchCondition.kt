package com.smartfoodnet.fnproduct.order.support.condition

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.Constants
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.order.entity.QCollectedOrder.collectedOrder
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDate
import java.time.LocalDateTime

class ConfirmProductSearchCondition(
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    var partnerId: Long? = null,

    @JsonFormat(pattern = Constants.DATE_FORMAT)
    @ApiModelProperty(value = "검색 시작일(수집일기준)")
    val collectedDateFrom: LocalDateTime? = null,

    @JsonFormat(pattern = Constants.DATE_FORMAT)
    @ApiModelProperty(value = "검색 종료일(수집일기준)")
    val collectedDateTo: LocalDateTime? = null,

    @ApiModelProperty(value = "묶음번호")
    val bundleNumber: String? = null,

    @ApiModelProperty(value = "주문번호")
    val orderNumber: String? = null,

    @ApiModelProperty(value = "쇼핑몰")
    val storeId: List<Long>? = null,

    @ApiModelProperty(value = "쇼핑몰상품명")
    val storeProductName: String? = null,

    @ApiModelProperty(value = "쇼핑몰상품코드")
    val storeProductCode: String? = null,

    @ApiModelProperty(value = "쇼핑몰옵션명")
    val storeOptionName: String? = null,

    @ApiModelProperty(value = "쇼핑몰옵션코드")
    val storeOptionCode: String? = null,

    @ApiModelProperty(value = "기본/모음상품명")
    val basicProductName: String? = null,

    @ApiModelProperty(value = "기본/모음상품코드")
    val basicProductCode: String? = null
) : PredicateSearchCondition() {
    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        return predicate.orAllOf(
            collectedOrder.partnerId.eq(partnerId),
            collectedOrder.status.eq(OrderStatus.ORDER_CONFIRM),
            if (collectedDateTo != null && collectedDateFrom != null) {
                collectedOrder.collectedAt.between(collectedDateFrom, collectedDateTo)
            } else null,
            bundleNumber?.let { collectedOrder.bundleNumber.eq(it) },
            orderNumber?.let { collectedOrder.orderNumber.eq(it) },
            storeId?.let { collectedOrder.storeId.`in`(it) },
            storeProductName?.let { collectedOrder.collectedProductInfo.collectedStoreProductName.contains(it) },
            storeProductCode?.let { collectedOrder.collectedProductInfo.collectedStoreProductCode.eq(it) },
            storeOptionName?.let { collectedOrder.collectedProductInfo.collectedStoreProductOptionName.contains(it) },
            storeOptionCode?.let { collectedOrder.collectedProductInfo.collectedStoreProductOptionCode.eq(it) }
        )
    }
}