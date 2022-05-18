package com.smartfoodnet.fnproduct.order.support.condition

import com.fasterxml.jackson.annotation.JsonFormat
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.Constants
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.claim.model.vo.ExchangeStatus
import com.smartfoodnet.fnproduct.claim.model.vo.ReturnStatus
import com.smartfoodnet.fnproduct.order.entity.QCollectedOrder.collectedOrder
import com.smartfoodnet.fnproduct.order.entity.QConfirmOrder.confirmOrder
import com.smartfoodnet.fnproduct.order.vo.DeliveryType
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDate

class CollectedOrderSearchCondition(
    @ApiModelProperty(value = "화주 (고객) ID", required = true)
    val partnerId: Long,

    @ApiModelProperty(value = "주문상태")
    val status: OrderStatus? = null,

    @ApiModelProperty(value = "주문번호")
    val orderNumber: String? = null,

    @ApiModelProperty(value = "출고번호")
    val orderCode: String? = null,

    @ApiModelProperty(value = "받는분 이름")
    val receiverName: String? = null,

    @ApiModelProperty(value = "쇼핑몰")
    val storeId: Long? = null,

    @ApiModelProperty(value = "쇼핑몰상품명")
    val storeProductName: String? = null,

    @ApiModelProperty(value = "쇼핑몰상품코드")
    val storeProductCode: String? = null,

    @ApiModelProperty(value = "기본/모음상품명")
    val basicProductName: String? = null,

    @ApiModelProperty(value = "기본/모음상품코드")
    val basicProductCode: String? = null,

    @ApiModelProperty(value = "반품 상태")
    val returnStatus: ReturnStatus? = null,

    @ApiModelProperty(value = "교환출고 상태")
    val exchangeStatus: ExchangeStatus? = null,

    @ApiModelProperty(value = "업로드 타입")
    val uploadType: OrderUploadType? = null,

    @ApiModelProperty(value = "배송 방식")
    val deliveryType: DeliveryType? = null,

    @JsonFormat(pattern = Constants.DATE_FORMAT)
    @ApiModelProperty(value = "수집일")
    val collectedAt: LocalDate? = null,

    @JsonFormat(pattern = Constants.DATE_FORMAT)
    @ApiModelProperty(value = "수집 시작일")
    val collectedFromDate: LocalDate? = null,

    @JsonFormat(pattern = Constants.DATE_FORMAT)
    @ApiModelProperty(value = "수집 마지막일")
    val collectedUntilDate: LocalDate? = null
) : PredicateSearchCondition() {
    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        return predicate.orAllOf(
            collectedOrder.partnerId.eq(partnerId),
            status?.let { collectedOrder.status.eq(it) },
            orderNumber?.let { collectedOrder.orderNumber.eq(it) },
            orderCode?.let { confirmOrder.orderCode.eq(it) },
            receiverName?.let { collectedOrder.receiver.name.eq(it) },
            storeId?.let { collectedOrder.storeId.eq(it) },
            storeProductName?.let { collectedOrder.collectedProductInfo.collectedStoreProductName.contains(it) },
            storeProductCode?.let { collectedOrder.collectedProductInfo.collectedStoreProductCode.eq(it) },
            basicProductName?.let { basicProduct.name.contains(it) },
            basicProductCode?.let { basicProduct.code.eq(it) },
            returnStatus?.let { collectedOrder.returnStatus.eq(it) },
            exchangeStatus?.let { collectedOrder.exchangeStatus.eq(it) },
            uploadType?.let { collectedOrder.uploadType.eq(it) },
            deliveryType?.let { collectedOrder.deliveryType.eq(it) },
            collectedAt?.let { collectedOrder.collectedAt.between(it.atStartOfDay(), it.plusDays(1).atStartOfDay()) },
            collectedFromDate?.let { collectedOrder.collectedAt.after(it.atStartOfDay()) },
            collectedUntilDate?.let { collectedOrder.collectedAt.before(it.atStartOfDay()) },
        )
    }
}
