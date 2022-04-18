package com.smartfoodnet.fnproduct.claim.model.vo

enum class ClaimStatus(val returnStatus: Int?, val description: String) {
    UNREGISTERED(null, "미등록"),
    RETURN_REQUESTED(1, "반품요청"),
    RETURN_IN_PROGRESS(3, "반품진행"),
    RETURN_INBOUND_COMPLETED(5, "반품입고완료"),
    RETURN_CANCELLED(9, "반품취소"),
    EXCHANGE_RELEASE_IN_PROGRESS(null, "교환출고중"),
    EXCHANGE_DELIVERY_COMPLETED(null, "교환배송완료");

    fun next(): ClaimStatus? {
        return when (this) {
            UNREGISTERED -> RETURN_REQUESTED
            RETURN_REQUESTED -> RETURN_IN_PROGRESS
            RETURN_IN_PROGRESS -> RETURN_INBOUND_COMPLETED
            RETURN_INBOUND_COMPLETED -> RETURN_CANCELLED
            EXCHANGE_RELEASE_IN_PROGRESS -> EXCHANGE_DELIVERY_COMPLETED
            EXCHANGE_DELIVERY_COMPLETED -> EXCHANGE_DELIVERY_COMPLETED
            RETURN_CANCELLED -> null
        }
    }
}

/**
 * 반품상태	                        교환출고상태
 * 반품요청/반품진행/반품입고완료/반품취소	교환출고중/교환배송완료
 */
enum class ReturnStatus(val returnStatus: Int?, val description: String) {
    UNREGISTERED(null, "미등록"),
    RETURN_REQUESTED(1, "반품요청"),
    RETURN_IN_PROGRESS(3, "반품진행"),
    RETURN_INBOUND_COMPLETED(5, "반품입고완료"),
    RETURN_CANCELLED(9, "반품취소")
}

enum class ExchangeStatus(val description: String) {
    UNREGISTERED("미등록"),
    EXCHANGE_RELEASE_IN_PROGRESS("교환출고중"),
    EXCHANGE_DELIVERY_COMPLETED("교환배송완료")
}
