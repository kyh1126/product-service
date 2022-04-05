package com.smartfoodnet.fnproduct.order.vo

enum class StoreSyncStatus(val code : Int, val description : String) {
    NONE(-1, "해당없음"),
    NEW(0, "신규주문"),
    INVOICE_PRINT(1, "송장출력"),
    INVOICE_WRITE(2, "송장입력"),
    SHIPPING(3, "출고"),
    TRANSIT(4, "배송중"),
    RECEIPT(5, "수취확인"),
    CALCULATE(6, "정산완료"),
    ORDER_CONFIRM(7, "주문확인"),
    PENDING(8, "보류"),
    CANCEL(9, "취소"),
    CANCEL_COMPLETE(10, "취소마감"),
    RECALL(11, "반품요청"),
    EXCHANGE(12, "교환요청"),
    RECALL_COMPLETE(13, "반품마감"),
    EXCHANGE_COMPLETE(14, "교환마감");
}