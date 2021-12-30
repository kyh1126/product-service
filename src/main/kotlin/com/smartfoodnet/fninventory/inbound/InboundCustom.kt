package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundSumDetail
import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundParent
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface InboundCustom {
    fun findInbounds(condition: InboundSearchCondition, page: Pageable): Page<GetInboundParent>
    fun findSumActualDetail(expectedIds: List<Long>) : List<GetInboundSumDetail>
}