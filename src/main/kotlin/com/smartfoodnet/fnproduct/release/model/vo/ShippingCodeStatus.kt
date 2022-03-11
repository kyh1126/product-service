package com.smartfoodnet.fnproduct.release.model.vo

enum class ShippingCodeStatus(val description: String) {
    UNREGISTERED("송장등록전"),
    REGISTERED("성공"),
    REGISTRATION_FAILED("송장등록실패")
}
