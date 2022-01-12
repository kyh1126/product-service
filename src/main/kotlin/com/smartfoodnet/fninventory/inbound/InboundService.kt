package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.apiclient.WmsClient
import com.smartfoodnet.apiclient.request.InboundWorkReadModel
import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.GetInboundWorkModel
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fninventory.inbound.model.dto.GetInbound
import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundParent
import com.smartfoodnet.fninventory.inbound.model.request.InboundCreateModel
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.exp
import kotlin.streams.toList

@Service
@Transactional(readOnly = true)
class InboundService(
    val inboundRepository: InboundRepository,
    val basicProductRepository: BasicProductRepository,
    val wmsClient: WmsClient
) : Log {

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
        var list : List<GetInbound> = listOf()
        if (parent.content.size > 0) {
            val actualDetail =
                inboundRepository.findSumActualDetail(parent.content.map { it.inboundExpectedId })
                    .associateBy { it.inboundExpectedId }

            list = parent.content.map {
                GetInbound.toDtoWithMerge(it, actualDetail[it.inboundExpectedId])
            }
        }

        return PageResponse.of(
            list,
            parent.totalElements,
            parent.pageable.pageNumber,
            list.size,
            page.sort
        )
    }

    fun getInboundActualDetail(partnerId: Long, expectedId: Long)
        = inboundRepository.findInboundActualDetail(partnerId, expectedId)

    fun getInboundWork(partnerId: Long, startDt: String, endDt: String, page: Int): CommonDataListModel<GetInboundWorkModel>? {
        val params = InboundWorkReadModel(
            memberId = partnerId,
            startDt = startDt,
            endDt = endDt,
            page = page
        )

        return wmsClient.getInboundWork(params).payload
    }

}