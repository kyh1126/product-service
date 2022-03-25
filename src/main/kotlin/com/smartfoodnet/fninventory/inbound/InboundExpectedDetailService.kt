package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.fninventory.inbound.entity.InboundExpectedDetail
import org.springframework.stereotype.Service

@Service
class InboundExpectedDetailService(
    val inboundExpectedDetailRepository: InboundExpectedDetailRepository
) {
    fun getInboundExpectedDetail(expectedDetailId: Long): InboundExpectedDetail {
        return inboundExpectedDetailRepository.findById(expectedDetailId).get()
    }
}