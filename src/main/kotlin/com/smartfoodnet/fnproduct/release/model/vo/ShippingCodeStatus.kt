package com.smartfoodnet.fnproduct.release.model.vo

import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import java.util.*

enum class ShippingCodeStatus(val description: String, val uploadType: OrderUploadType?) {
    NONE("송장없음", null),
    REGISTERED("자동등록성공", OrderUploadType.API),
    REGISTER_FAILED("자동등록실패", OrderUploadType.API),
    BEFORE_REGISTER("자동등록전", OrderUploadType.API),
    WAITING_CALLBACK("자동등록전(콜백대기중)", OrderUploadType.API),
    EXCEL_REGISTER("수기등록", OrderUploadType.UPLOAD);

    fun isInProgress(): Boolean {
        return this == REGISTERED || this == REGISTER_FAILED || this in INITIAL_STATUSES
    }

    companion object {
        private val INITIAL_STATUSES = EnumSet.of(BEFORE_REGISTER, EXCEL_REGISTER)

        fun getInitialStatus(uploadType: OrderUploadType): ShippingCodeStatus {
            return INITIAL_STATUSES.firstOrNull { it.uploadType == uploadType }
                ?: throw IllegalArgumentException("Initial ShippingCodeStatus 을 찾을 수 없습니다")
        }
    }
}
