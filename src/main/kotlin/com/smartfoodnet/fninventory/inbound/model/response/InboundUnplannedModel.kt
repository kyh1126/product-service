package com.smartfoodnet.fninventory.inbound.model.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fninventory.inbound.entity.InboundUnplanned
import java.time.LocalDateTime

data class InboundUnplannedModel(
    val receivingWorkHistoryId : Long,
    val basicProductId : Long? = null,
    val basicProductName : String? = null,
    val basicProductCode : String? = null,
    val actualInboundDate: LocalDateTime,
    val actualQuantity: Long,
    val boxQuantity: Long? = null,
    val palletQuantity: Long? = null,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val manufactureDate: LocalDateTime? = null,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val expirationDate: LocalDateTime? = null
){
    companion object{
        fun fromEntity(inboundUnplanned: InboundUnplanned) : InboundUnplannedModel{
            return InboundUnplannedModel(
                receivingWorkHistoryId = inboundUnplanned.receivingWorkHistoryId,
                basicProductId = inboundUnplanned.basicProduct!!.id,
                basicProductName = inboundUnplanned.basicProduct!!.name,
                basicProductCode = inboundUnplanned.basicProduct!!.code,
                actualInboundDate = inboundUnplanned.workDate,
                actualQuantity = inboundUnplanned.quantity.toLong(),
                boxQuantity = inboundUnplanned.boxQuantity?.toLong(),
                palletQuantity = inboundUnplanned.palletQuantity?.toLong(),
                manufactureDate = inboundUnplanned.makeDate,
                expirationDate = inboundUnplanned.expireDate
            )
        }
    }
}