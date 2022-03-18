package com.smartfoodnet.fnproduct.order.support.condition

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.Constants
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.order.entity.QCollectedOrder.collectedOrder
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
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
            collectedOrder.status.eq(OrderStatus.NEW),
            collectedAt?.let { collectedOrder.collectedAt.between(it.atStartOfDay(), it.plusDays(1).atStartOfDay()) },
            bundleNumber?.let { collectedOrder.bundleNumber.eq(it) },
            orderNumber?.let { collectedOrder.orderNumber.eq(it)},
            mappedFlag?.let { if (mappedFlag) basicProduct.isNotNull else basicProduct.isNull },
            storeId?.let { collectedOrder.storeId.eq(it) },
            storeProductName?.let { collectedOrder.collectedProductInfo.collectedStoreProductName.contains(it) },
            storeProductCode?.let { collectedOrder.collectedProductInfo.collectedStoreProductCode.eq(it) }
        )
    }
}
