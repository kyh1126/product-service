package com.smartfoodnet.fnproduct.order.support

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.Constants
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.order.entity.QCollectedOrder.collectedOrder
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDate

class CollectingOrderSearchCondition(
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    var partnerId : Long? = null,

    @JsonFormat(pattern = Constants.DATE_FORMAT)
    @ApiModelProperty(value = "수집일")
    val collectedAt : LocalDate? = null,

    @ApiModelProperty(value = "묶음번호")
    val bundleNumber: String? = null,

    @ApiModelProperty(value = "주문번호")
    val orderNumber: String? = null,

    @ApiModelProperty(value = "매칭여부")
    val mappedFlag: Boolean? = null,

    @ApiModelProperty(value = "쇼핑몰")
    val storeId: Long? = null,

    @ApiModelProperty(value = "쇼핑몰상품명")
    val storeProductName: String? = null,

    @ApiModelProperty(value = "쇼핑몰상품코드")
    val storeProductCode: String? = null

) : PredicateSearchCondition(){
    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        return predicate.orAllOf(
            collectedOrder.partnerId.eq(partnerId),
            collectedAt?.let { collectedOrder.collectedAt.between(it.atStartOfDay(), it.plusDays(1).atStartOfDay()) },
            bundleNumber?.let { collectedOrder.bundleNumber.eq(it) },
            orderNumber?.let { collectedOrder.orderNumber.eq(it)},
            mappedFlag?.let { if (mappedFlag) collectedOrder.storeProduct.isNotNull else collectedOrder.storeProduct.isNull },
            storeId?.let { collectedOrder.storeId.eq(it) },
            storeProductName?.let { collectedOrder.collectedProductInfo.collectedStoreProductName.contains(it) },
            storeProductCode?.let { collectedOrder.collectedProductInfo.collectedStoreProductCode.eq(it) }
        )
    }
}

//class CollectedOrderSearchCondition(
//    @ApiModelProperty(hidden = true)
//    @JsonIgnore
//    var partnerId: Long? = null,
//
//    @ApiModelProperty(value = "쇼핑몰 id 리스트")
//    var storeIds: List<Long>? = null,
//
//    @ApiModelProperty(value = "주문 상태 리스트")
//    var orderStates: List<OrderStatus>? = null,
//
//    @ApiModelProperty(value = "주문기간 시작일")
//    var startingAt: LocalDateTime? = null,
//
//    @ApiModelProperty(value = "주문기간 종료일")
//    var endingAt: LocalDateTime? = null,
//
//    @ApiModelProperty(value = "매칭상품명")
//    var basicProductName: String? = null,
//
//    @ApiModelProperty(value = "쇼핑몰상품명")
//    var storeProductName: String? = null,
//
//    @ApiModelProperty(value = "매칭상품코드")
//    var basicProductId: Long? = null,
//
//    @ApiModelProperty(value = "쇼핑몰상품코드 (각 쇼핑몰에서 할당된 코드)")
//    var storeProductCode: String? = null,
//
//    ) : PredicateSearchCondition() {
//
//    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
//        return predicate.orAllOf(
//            eqPartnerId(partnerId),
//            inOrderStates(orderStates),
//            inStoreIds(storeIds),
//            betweenOrderedAt(startingAt, endingAt),
//            likeBasicProductName(basicProductName),
//            likeStoreProductName(storeProductName),
//            eqBasicProductId(basicProductId),
//            eqStoreProductCode(storeProductCode)
//        )
//    }
//
//    private fun eqPartnerId(partnerId: Long?) = partnerId?.let { orderDetail.partnerId.eq(it) }
//
//    private fun inOrderStates(orderStates: Collection<OrderStatus>?) =
//        orderStates?.let { orderDetail.status.`in`(orderStates) }
//
//    private fun inStoreIds(storeIds: Collection<Long>?) =
//        storeIds?.let { orderDetail.storeId.`in`(storeIds) }
//
//    private fun betweenOrderedAt(startingAt: LocalDateTime?, endingAt: LocalDateTime?): BooleanExpression? {
//        if (startingAt != null && endingAt != null) {
//            return orderDetail.orderedAt.between(startingAt, endingAt)
//        }
//        return null
//    }
//
//    private fun likeBasicProductName(basicProductName: String?) =
//        basicProductName?.let { orderDetail.storeProduct.basicProduct.name.likeIgnoreCase("%$basicProductName%") }
//
//    private fun likeStoreProductName(storeProductName: String?) =
//        storeProductName?.let { orderDetail.storeProduct.name.likeIgnoreCase("%$storeProductName%") }
//
//    private fun eqStoreProductCode(storeProductCode: String?) =
//        storeProductCode?.let { orderDetail.storeProduct.storeProductCode.eq(storeProductCode) }
//
//    private fun eqBasicProductId(basicProductId: Long?) =
//        basicProductId?.let { orderDetail.storeProduct.basicProduct.id.eq(basicProductId) }
//}
