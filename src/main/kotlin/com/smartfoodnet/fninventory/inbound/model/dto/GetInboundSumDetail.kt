package com.smartfoodnet.fninventory.inbound.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.smartfoodnet.common.Constants
import java.time.LocalDateTime

data class GetInboundSumDetail(
    val inboundExpectedId: Long,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val actualInboundDate: LocalDateTime? = null,
    val actualInboundDateCount: Long? = null,
    val actualQuantity: Long? = null,
    val boxQuantity: Long? = null,
    val palletQuantity: Long? = null,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val manufactureDate: LocalDateTime? = null,
    val manufactureDateCount: Long? = null,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val expirationDate: LocalDateTime? = null,
    val expirationDateCount: Long? = null,
)