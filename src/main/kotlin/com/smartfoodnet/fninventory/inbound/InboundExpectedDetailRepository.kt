package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.fninventory.inbound.entity.InboundExpectedDetail
import org.springframework.data.jpa.repository.JpaRepository

interface InboundExpectedDetailRepository : JpaRepository<InboundExpectedDetail, Long>