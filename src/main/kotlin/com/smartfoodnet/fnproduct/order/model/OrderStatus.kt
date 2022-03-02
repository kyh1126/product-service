package com.smartfoodnet.fnproduct.order.model


enum class OrderStatus(val code: Int, val description: String) {
    NEW(0, "신규주문"),
    ORDER_CONFIRM(1, "주문확인"),
    RELEASE_REGISTRATION(2, "출고등록완료"),
    RELEASE_WORKING(3, "출고작업중"),
    IN_TRANSIT(4, "배송중"),
    COMPLETE(5, "배송완료"),
    CANCEL(9, "취소");

    fun next() = when (this) {
        NEW -> ORDER_CONFIRM
        ORDER_CONFIRM -> RELEASE_REGISTRATION
        RELEASE_REGISTRATION -> RELEASE_WORKING
        RELEASE_WORKING -> IN_TRANSIT
        IN_TRANSIT -> COMPLETE
        COMPLETE -> COMPLETE
        CANCEL -> CANCEL
    }

    fun previous() = when (this){
        NEW -> NEW
        ORDER_CONFIRM -> NEW
        RELEASE_REGISTRATION -> ORDER_CONFIRM
        RELEASE_WORKING -> RELEASE_REGISTRATION
        IN_TRANSIT -> RELEASE_WORKING
        COMPLETE -> IN_TRANSIT
        CANCEL -> CANCEL
    }

    fun cancel() : OrderStatus{
        return CANCEL
    }
}