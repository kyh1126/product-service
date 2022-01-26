package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import com.smartfoodnet.fninventory.inbound.model.response.InboundUnplannedModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class InboundUnplannedService(
    val inboundUnplannedRepository: InboundUnplannedRepository
) {
    fun getInboundUnplanned(
        condition: InboundSearchCondition,
        page: Pageable
    ): PageResponse<InboundUnplannedModel> {
        return inboundUnplannedRepository.findInboundUnplanned(condition, page)
            .map { InboundUnplannedModel.fromEntity(it) }
            .run { PageResponse.of(this) }
    }
}