package com.smartfoodnet.fnproduct.release.model.vo

import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import java.util.*

enum class ShippingCodeStatus(val description: String, val uploadType: OrderUploadType) {
    REGISTERED("자동송장등록성공", OrderUploadType.API),
    REGISTER_FAILED("자동송장등록실패", OrderUploadType.API),
    BEFORE_REGISTER("자동송장등록전", OrderUploadType.API),
    EXCEL_REGISTER("수기등록", OrderUploadType.UPLOAD);

    companion object {
        private val INITIAL_STATUSES = EnumSet.of(BEFORE_REGISTER, EXCEL_REGISTER)

        fun getInitialStatus(uploadType: OrderUploadType): ShippingCodeStatus? {
            return INITIAL_STATUSES.firstOrNull { it.uploadType == uploadType }
        }
    }
}
