package com.smartfoodnet.fninventory.inbound.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.smartfoodnet.common.Constants
import java.time.LocalDateTime

data class CreateInboundResponse (
    val id: Long,
    val partnerId: Long,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val createdAt: LocalDateTime?
)