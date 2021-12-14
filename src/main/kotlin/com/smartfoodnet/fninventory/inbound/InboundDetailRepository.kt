package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.fninventory.inbound.entity.InboundDetail
import org.springframework.data.jpa.repository.JpaRepository

interface InboundDetailRepository: JpaRepository<InboundDetail, Long> {
    fun findByInboundId(id: Long) : List<InboundDetail>
}