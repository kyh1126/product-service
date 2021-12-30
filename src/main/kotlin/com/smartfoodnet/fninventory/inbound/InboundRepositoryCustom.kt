package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundActualDetail
import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundSumDetail
import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundParent
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface InboundRepositoryCustom {
    fun findInbounds(condition: InboundSearchCondition, page: Pageable): Page<GetInboundParent>
    fun findInboundActualDetail(partnerId: Long, expectedId: Long): List<GetInboundActualDetail>
    fun findSumActualDetail(expectedIds: List<Long>) : List<GetInboundSumDetail>
}