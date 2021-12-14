package com.smartfoodnet.fnproduct.order.support

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.common.collect.Lists
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.order.entity.QOrderDetail.orderDetail
import com.smartfoodnet.fnproduct.order.model.OrderStatus
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

class OrderSearchCondition(
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    var partnerId: Long? = null,

    @ApiModelProperty(value = "쇼핑몰 id 리스트")
    var storeIds: List<Long>? = Lists.newArrayList(),

    @ApiModelProperty(value = "주문 상태 리스트")
    var orderStates: List<OrderStatus>? = Lists.newArrayList(),

    @ApiModelProperty(value = "주문기간 시작일")
    var startingAt: LocalDateTime? = null,

    @ApiModelProperty(value = "주문기간 종료일")
    var endingAt: LocalDateTime? = null,

    @ApiModelProperty(value = "매칭상품명")
    var basicProductName: String? = null,

    @ApiModelProperty(value = "쇼핑몰상품명")
    var storeProductName: String? = null,

    @ApiModelProperty(value = "매칭상품코드")
    var basicProductId: Long? = null,

    @ApiModelProperty(value = "쇼핑몰상품코드 (각 쇼핑몰에서 할당된 코드)")
    var storeProductCode: String? = null,

    ) : PredicateSearchCondition() {

    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        return predicate.orAllOf(
            eqPartnerId(partnerId),
            inOrderStates(orderStates),
            inStoreIds(storeIds),
            betweenOrderedAt(startingAt, endingAt),
            eqBasicProductName(basicProductName),
            eqStoreProductName(storeProductName),
            eqBasicProductId(basicProductId),
            eqStoreProductCode(storeProductCode)
        )
    }

    private fun eqPartnerId(partnerId: Long?) = partnerId?.let { orderDetail.partnerId.eq(it) }

    private fun inOrderStates(orderStates: Collection<OrderStatus>?) =
        orderDetail.status.`in`(orderStates)

    private fun inStoreIds(storeIds: Collection<Long>?) =
        orderDetail.storeId.`in`(storeIds)

    private fun betweenOrderedAt(startingAt: LocalDateTime?, endingAt: LocalDateTime?): BooleanExpression? {
        return orderDetail.orderedAt.between(startingAt, endingAt)
    }

    private fun eqBasicProductName(basicProductName: String?) =
        basicProductName?.let { orderDetail.storeProduct.basicProduct.name.eq(basicProductName) }

    private fun eqStoreProductName(storeProductName: String?) =
        storeProductName?.let { orderDetail.storeProduct.name.eq(storeProductName) }

    private fun eqStoreProductCode(storeProductCode: String?) =
        storeProductCode?.let { orderDetail.storeProduct.storeProductCode.eq(storeProductCode) }

    private fun eqBasicProductId(basicProductId: Long?) =
        basicProductId?.let { orderDetail.storeProduct.basicProduct.id.eq(basicProductId) }


}
