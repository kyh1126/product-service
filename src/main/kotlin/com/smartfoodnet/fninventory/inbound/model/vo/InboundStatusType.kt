package com.smartfoodnet.fninventory.inbound.model.vo

enum class InboundStatusType(
    val description: String
) {
    EXPECTED("입고예정"),
    COMPLETE("입고완료"),
    CANCEL("입고취소");
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