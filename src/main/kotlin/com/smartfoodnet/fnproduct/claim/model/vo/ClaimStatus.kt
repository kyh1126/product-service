package com.smartfoodnet.fnproduct.claim.model.vo

import com.smartfoodnet.common.error.exception.NoSuchElementError

/**
 * 반품상태	                        교환출고상태
 * 반품요청/반품진행/반품입고완료/반품취소	교환출고중/교환배송완료
 */
enum class ReturnStatus(val code: Int?, val description: String) {
    UNREGISTERED(null, "미등록"),
    RETURN_REQUESTED(1, "반품요청"),
    RETURN_IN_PROGRESS(3, "반품진행"),
    RETURN_INBOUND_COMPLETED(5, "반품입고완료"),
    RETURN_CANCELLED(9, "반품취소");

    companion object {
        fun fromCode(code: Int?): ReturnStatus {
            code ?: throw NoSuchElementError("returnStatus code is null")

            return values().firstOrNull { it.code == code }
                ?: throw IllegalArgumentException("Format $code is illegal")
        }
    }
}

enum class ExchangeStatus(val description: String) {
    UNREGISTERED("미등록"),
    EXCHANGE_RELEASE_IN_PROGRESS("교환출고중"),
    EXCHANGE_DELIVERY_COMPLETED("교환배송완료");
}
