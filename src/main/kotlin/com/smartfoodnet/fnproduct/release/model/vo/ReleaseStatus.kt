package com.smartfoodnet.fnproduct.release.model.vo

import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import java.util.*

enum class ReleaseStatus(val releaseStatus: Int?, val description: String, val orderStatus: OrderStatus) {
    BEFORE_RELEASE_REQUEST(null, "출고요청전", OrderStatus.BEFORE_RELEASE_REQUEST),
    RELEASE_REQUESTED(1, "출고요청", OrderStatus.RELEASE_REQUESTED),
    RELEASE_ORDERED(3, "출고지시", OrderStatus.RELEASE_ORDERED),
    RELEASE_IN_PROGRESS(5, "출고작업중", OrderStatus.RELEASE_IN_PROGRESS),
    DELIVERY_IN_TRANSIT(7, "출고완료", OrderStatus.IN_TRANSIT),
    DELIVERY_COMPLETED(7, "출고완료", OrderStatus.COMPLETE),
    RELEASE_CANCELLED(9, "출고취소", OrderStatus.RELEASE_CANCELLED);

    companion object {
        val SYNCABLE_STATUSES: EnumSet<ReleaseStatus> =
            EnumSet.of(BEFORE_RELEASE_REQUEST, RELEASE_REQUESTED, RELEASE_ORDERED, RELEASE_IN_PROGRESS)

        val DELIVERY_SYNCABLE_STATUSES: EnumSet<ReleaseStatus> =
            EnumSet.of(RELEASE_ORDERED, RELEASE_IN_PROGRESS, DELIVERY_IN_TRANSIT)

        fun fromReleaseStatus(releaseStatus: Int): ReleaseStatus {
            return values().firstOrNull { it.releaseStatus == releaseStatus }
                ?: throw IllegalArgumentException("Format $releaseStatus is illegal")
        }
    }
}
