package com.smartfoodnet.fninventory.inbound.model.vo

// 입고상태
enum class InboundStatusType(description: String) {
    EXPECTED("입고예정"),
    COMPLETE("입고완료"),
    CANCEL("입고취소")
}