package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.fninventory.inbound.entity.InboundUnplanned
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import com.smartfoodnet.fninventory.inbound.model.request.InboundUnplannedSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface InboundUnplannedRepositoryCustom {
    fun findInboundUnplanned(condition: InboundUnplannedSearchCondition, page: Pageable): Page<InboundUnplanned>
}