package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.fninventory.inbound.entity.InboundUnplanned
import org.springframework.data.jpa.repository.JpaRepository

interface InboundUnplannedRepository : JpaRepository<InboundUnplanned, Long>