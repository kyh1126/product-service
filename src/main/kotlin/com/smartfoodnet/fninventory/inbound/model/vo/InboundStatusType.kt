package com.smartfoodnet.fninventory.inbound.model.vo

enum class InboundStatusType(
    val code: Int,
    val description: String
) {
    EXPECTED(1,"입고예정"),
    COMPLETE(3,"입고완료"),
    CANCEL(4,"입고취소");

    companion object{
        fun getInboundStatusType(code: Int) : InboundStatusType {
            return values().associateBy { it.code }[code]
                ?: throw IllegalArgumentException("InboundStatusType을 찾을 수 없습니다")
        }
    }
}

enum class InboundStatusAdvanceType(
    val code: Int,
    val description: String
){
    INBOUND(1, "입고완료"),
    INBOUND_LOCATION(3, "적치"),
    FORWARD(5,"회송"),
    RETURN(7,"반품입고"),
    CANCEL(9,"입고취소");

    companion object{
        private fun getInbound(code:Int) : InboundStatusAdvanceType?{
            return values().associateBy{it.code}[code]
        }

        fun getInboundDescription(code: Int): String?{
            return getInbound(code)?.description
        }
    }
}