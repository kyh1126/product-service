package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.fninventory.inbound.entity.InboundActualDetail
import org.springframework.data.jpa.repository.JpaRepository

interface InboundActualDetailRepository : JpaRepository<InboundActualDetail, Long>{
    fun existsByReceivingWorkHistoryId(receivingHistoryId : Long) : Boolean
}