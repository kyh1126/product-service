package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.request.InboundWorkReadModel
import com.smartfoodnet.apiclient.request.PlanProduct
import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.GetInboundWorkModel
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fninventory.inbound.model.dto.GetInbound
import com.smartfoodnet.fninventory.inbound.model.request.InboundCreateModel
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.RuntimeException

@Service
@Transactional(readOnly = true)
class InboundService(
    val inboundRepository: InboundRepository,
    val basicProductRepository: BasicProductRepository,
    val nosnosClientService: NosnosClientService
) : Log {

    @Transactional
    fun createInbound(createModel: InboundCreateModel){
        val inbound = createModel.toEntity()
        createModel.expectedList.forEach {
            val basicProduct = getBasicProduct(it.basicProductId)
            inbound.addExptecdItem(it.toEntity(basicProduct))
        }

        nosnosClientService.sendInboundAndSetRegisterNo(inbound)

        inboundRepository.save(inbound)
    }

    private fun getBasicProduct(basicProductCode : String) : BasicProduct {
        return basicProductRepository.findByCode(basicProductCode)?:throw NoSuchElementException("기본상품(${basicProductCode})을 찾을 수 없습니다.")
    }

    private fun getBasicProduct(basicProductId : Long) : BasicProduct {
        return basicProductRepository.findById(basicProductId).get()
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
}