package com.smartfoodnet.fnproduct.order.vo


enum class OrderStatus(val description: String) {
    NEW("신규주문"),
    ORDER_CONFIRM("주문접수완료"),
    BEFORE_RELEASE_REQUEST("출고작업중 (1/4)"),
    RELEASE_REQUESTED("출고작업중 (2/4)"),
    RELEASE_ORDERED("출고작업중 (3/4)"),
    RELEASE_IN_PROGRESS("출고작업중 (4/4)"),
    IN_TRANSIT("배송중"),
    COMPLETE("배송완료"),
    RELEASE_PAUSED("출고중지"),
    RELEASE_CANCELLED("출고취소"),
    CANCEL("주문취소");

    fun next() = when (this) {
        NEW -> ORDER_CONFIRM
        ORDER_CONFIRM -> BEFORE_RELEASE_REQUEST
        BEFORE_RELEASE_REQUEST -> RELEASE_REQUESTED
        RELEASE_REQUESTED -> RELEASE_ORDERED
        RELEASE_ORDERED -> RELEASE_IN_PROGRESS
        RELEASE_IN_PROGRESS -> IN_TRANSIT
        IN_TRANSIT -> COMPLETE
        else -> this
    }

    fun cancel(): OrderStatus {
        return CANCEL
    }
}
