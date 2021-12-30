package com.smartfoodnet.fninventory.inbound.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.querydsl.core.annotations.QueryProjection
import com.smartfoodnet.common.Constants
import java.time.LocalDateTime

data class GetInboundActualDetail @QueryProjection constructor(
    val inboundActualId: Long,
    val inboundExpectedId: Long,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val actualInboundDate: LocalDateTime? = null,
    val actualQuantity: Long? = null,
    val boxQuantity: Long? = null,
    val palletQuantity: Long? = null,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val manufactureDate: LocalDateTime? = null,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val expirationDate: LocalDateTime? = null,
)
