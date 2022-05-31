package com.smartfoodnet.fnproduct.release.model.vo

import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.order.vo.ShippingMethodType
import java.util.*

enum class ReleaseStatus(val nosnosReleaseStatus: Int?, val description: String, val orderStatus: OrderStatus) {
    BEFORE_RELEASE_REQUEST(null, "출고요청전", OrderStatus.BEFORE_RELEASE_REQUEST),
    RELEASE_REQUESTED(1, "출고요청", OrderStatus.RELEASE_REQUESTED),
    RELEASE_ORDERED(3, "출고지시", OrderStatus.RELEASE_ORDERED),
    RELEASE_IN_PROGRESS(5, "출고작업중", OrderStatus.RELEASE_IN_PROGRESS),
    DELIVERY_IN_TRANSIT(7, "출고완료", OrderStatus.IN_TRANSIT),
    DELIVERY_COMPLETED(7, "출고완료", OrderStatus.COMPLETE),
    RELEASE_PAUSED(9, "출고취소", OrderStatus.RELEASE_PAUSED),
    RELEASE_CANCELLED(9, "출고취소", OrderStatus.RELEASE_CANCELLED);

    companion object {
        val SYNCABLE_STATUSES: EnumSet<ReleaseStatus> =
            EnumSet.of(BEFORE_RELEASE_REQUEST, RELEASE_REQUESTED, RELEASE_ORDERED, RELEASE_IN_PROGRESS)

        val DELIVERY_SYNCABLE_STATUSES: EnumSet<ReleaseStatus> =
            EnumSet.of(RELEASE_ORDERED, RELEASE_IN_PROGRESS, DELIVERY_IN_TRANSIT)

        val NOSNOS_CANCELLED_STATUSES: EnumSet<ReleaseStatus> =
            EnumSet.of(RELEASE_PAUSED, RELEASE_CANCELLED)

        fun fromNosnosReleaseStatus(nosnosReleaseStatus: Int, shippingMethodId: Int?): ReleaseStatus {
            val releaseStatus = values().firstOrNull { it.nosnosReleaseStatus == nosnosReleaseStatus }
                ?: throw IllegalArgumentException("Format $nosnosReleaseStatus is illegal")

            // 택배 외 출고방식의 경우, 배송완료로 처리 (배송중 x)
            if (releaseStatus == DELIVERY_IN_TRANSIT && !ShippingMethodType.isParcel(shippingMethodId)) {
                return DELIVERY_COMPLETED
            }
            return releaseStatus
        }

        fun fromOrderStatus(orderStatus: OrderStatus?): ReleaseStatus? {
            if (orderStatus == null) return null
            return values().firstOrNull { it.orderStatus == orderStatus }
        }
    }
}
