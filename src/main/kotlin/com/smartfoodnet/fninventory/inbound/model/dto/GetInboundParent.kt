package com.smartfoodnet.fninventory.inbound.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.querydsl.core.annotations.QueryProjection
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fninventory.inbound.model.vo.InboundMethodType
import com.smartfoodnet.fninventory.inbound.model.vo.InboundStatusType
import java.time.LocalDateTime

data class GetInboundParent @QueryProjection constructor(
    val inboundId: Long,
    val inboundExpectedId: Long,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val createDate : LocalDateTime,
    val inboundStatusType: InboundStatusType,
    val registrationNo: String? = null,
    val registrationId: Long? = null,
    val basicProductId: Long? = null,
    val basicProductCode: String? = null,
    val basicProductName : String? = null,
    val inboundMethod: InboundMethodType? = null,
    val inboundExpectedDate: LocalDateTime? = null,
    val requestQuantity: Long? = null
)