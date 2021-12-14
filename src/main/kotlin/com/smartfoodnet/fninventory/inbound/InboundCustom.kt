package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.fninventory.inbound.entity.Inbound
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface InboundCustom {
    fun findByPartnerId(condition: InboundSearchCondition, page: Pageable): Page<Inbound>
}