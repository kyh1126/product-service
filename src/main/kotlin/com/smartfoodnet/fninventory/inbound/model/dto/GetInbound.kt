package com.smartfoodnet.fninventory.inbound.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fninventory.inbound.model.vo.InboundMethodType
import com.smartfoodnet.fninventory.inbound.model.vo.InboundStatusType
import java.time.LocalDateTime

data class GetInbound (
    @JsonUnwrapped
    val parent: GetInboundParent,
    val sumDetail: GetInboundSumDetail? = null
){
    companion object{
        fun toDtoWithMerge(inboundParent: GetInboundParent, inboundSumDetail: GetInboundSumDetail?)
        = GetInbound(
            inboundParent,
            inboundSumDetail
        )
    }
}