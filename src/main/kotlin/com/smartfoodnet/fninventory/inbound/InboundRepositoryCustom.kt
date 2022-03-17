package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.fninventory.inbound.entity.Inbound
import com.smartfoodnet.fninventory.inbound.entity.InboundExpectedDetail
import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundSumDetail
import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundParent
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface InboundRepositoryCustom {
    fun findInbounds(condition: InboundSearchCondition, page: Pageable): Page<GetInboundParent>
    fun findStatusExpectedInbounds(basicDate : LocalDateTime) : List<Inbound>
    fun findSumActualDetail(expectedIds: List<Long>) : List<GetInboundSumDetail>
    fun findInboundExpectedWithBasicProduct(receivingPlanId: Long, shippingProductId : Long) : InboundExpectedDetail?
}