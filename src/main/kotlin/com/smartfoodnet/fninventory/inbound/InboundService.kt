package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fninventory.inbound.model.request.InboundCreateModel
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import com.smartfoodnet.fninventory.inbound.model.response.InboundModel
import com.smartfoodnet.fninventory.inbound.model.vo.InboundStatusType
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class InboundService(
    val inboundRepository: InboundRepository,
    val basicProductRepository: BasicProductRepository
) {

    fun createInbound(list: List<InboundCreateModel>) {
        list.forEach {
            val inbound = it.toEntity()
            inbound.basicProduct = basicProductRepository.findByCode(it.basicProductCode!!)?:throw NoSuchElementException("기본상품을 찾을 수 없습니다")
            inboundRepository.save(inbound)
        }
    }

    @Transactional
    fun cancelInbound(inboundId: Long) {
        val inbound = inboundRepository.findById(inboundId).get()
        if (inbound.status != InboundStatusType.EXPECTED){
            throw IllegalStateException("입고예정 상태에서만 취소가 가능합니다")
        }
        inbound.status = InboundStatusType.CANCEL
        inbound.deletedAt = LocalDateTime.now()
    }

    fun getInbound(inboundId: Long): InboundModel {
        val inbound = inboundRepository.findById(inboundId).get()
        return InboundModel.fromEntity(inbound)
    }

    fun getInbounds(condition: InboundSearchCondition, page: Pageable): PageResponse<InboundModel> {
        return inboundRepository.findByPartnerId(condition, page)
            .map(InboundModel::fromEntity)
            .run { PageResponse.of(this) }
    }

}