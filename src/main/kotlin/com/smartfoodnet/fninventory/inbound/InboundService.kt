package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fninventory.inbound.model.dto.GetInbound
import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundParent
import com.smartfoodnet.fninventory.inbound.model.request.InboundCreateModel
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.streams.toList

@Service
@Transactional(readOnly = true)
class InboundService(
    val inboundRepository: InboundRepository,
    val basicProductRepository: BasicProductRepository
){
    @Transactional
    fun createInbound(createModel: InboundCreateModel){
        val inbound = createModel.toEntity()
        createModel.expectedList.forEach {
            val basicProduct =  basicProductRepository.findByCode(it.basicProductCode)?:throw NoSuchElementException("기본상품(${it.basicProductCode})을 찾을 수 없습니다.")
            inbound.addExptecdItem(it.toEntity(basicProduct))
        }
        inboundRepository.save(inbound)
    }

    fun getInbound(condition: InboundSearchCondition, page: Pageable) : PageResponse<GetInbound>?{
        val parent = inboundRepository.findInbounds(condition, page)
        val actualDetail = inboundRepository.findSumActualDetail(parent.content.map { it.inboundExpectedId }).associateBy { it.inboundExpectedId }

        val list = parent.content.map{
            GetInbound.toDtoWithMerge(it, actualDetail[it.inboundExpectedId])
        }

        return PageResponse.of(list, parent.totalElements, parent.pageable.pageNumber, list.size, page.sort)
    }

}