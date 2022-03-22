package com.smartfoodnet.fnproduct.release.model.vo

import com.smartfoodnet.fnproduct.order.vo.OrderStatus

enum class ReleaseStatus(val releaseStatus: Int?, val description: String, val orderStatus: OrderStatus? = null) {
    RELEASE_REQUESTED(1, "출고요청", OrderStatus.RELEASE_REGISTRATION),
    RELEASE_ORDERED(3, "출고지시", OrderStatus.RELEASE_WORKING),
    RELEASE_IN_PROGRESS(5, "출고작업중", OrderStatus.IN_TRANSIT),
    RELEASE_COMPLETED(7, "출고완료", OrderStatus.COMPLETE),
    RELEASE_CANCELED(9, "출고취소", OrderStatus.CANCEL),
}
