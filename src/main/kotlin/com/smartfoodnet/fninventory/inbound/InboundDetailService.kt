package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.fninventory.inbound.model.response.InboundDetailModel
import org.springframework.stereotype.Service

@Service
class InboundDetailService(
    val inboundDetailRepository: InboundDetailRepository
) {

    fun getDetail(inboundId: Long) =
        inboundDetailRepository.findByInboundId(inboundId).map(InboundDetailModel::fromEntity)

}