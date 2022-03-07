package com.smartfoodnet.fnproduct.order.support.condition

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.Constants
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.order.entity.QCollectedOrder
import com.smartfoodnet.fnproduct.order.entity.QCollectedOrder.collectedOrder
import com.smartfoodnet.fnproduct.order.entity.QConfirmOrder.confirmOrder
import com.smartfoodnet.fnproduct.order.model.OrderStatus
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDate

class ConfirmOrderSearchCondition(
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    var partnerId: Long? = null,

    @JsonFormat(pattern = Constants.DATE_FORMAT)
    @ApiModelProperty(value = "수집일")
    val collectedAt: LocalDate? = null,

    @ApiModelProperty(value = "묶음번호")
    val bundleNumber: String? = null,

    @ApiModelProperty(value = "주문번호")
    val orderNumber: String? = null,

    @ApiModelProperty(value = "쇼핑몰")
    val storeId: Long? = null,

    @ApiModelProperty(value = "쇼핑몰상품명")
    val storeProductName: String? = null,

    @ApiModelProperty(value = "쇼핑몰상품코드")
    val storeProductCode: String? = null,

    @ApiModelProperty(value = "기본/모음상품명")
    val basicProductName: String? = null,

    @ApiModelProperty(value = "기본/모음상품코드")
    val basicProductCode: String? = null
) : PredicateSearchCondition() {
    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        return predicate.orAllOf(
            confirmOrder.partnerId.eq(partnerId),
            collectedOrder.status.eq(OrderStatus.ORDER_CONFIRM),
            collectedAt?.let {
                collectedOrder.collectedAt.between(
                    it.atStartOfDay(),
                    it.plusDays(1).atStartOfDay()
                )
            },
            bundleNumber?.let { confirmOrder.bundleNumber.eq(it) },
            orderNumber?.let { collectedOrder.orderNumber.eq(it) },
            storeId?.let { collectedOrder.storeId.eq(it) },
            storeProductName?.let { collectedOrder.collectedProductInfo.collectedStoreProductName.contains(it) },
            storeProductCode?.let { collectedOrder.collectedProductInfo.collectedStoreProductCode.eq(it) }
        )
    }
}