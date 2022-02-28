package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fninventory.inbound.model.dto.CreateInboundResponse
import com.smartfoodnet.fninventory.inbound.model.dto.GetInbound
import com.smartfoodnet.fninventory.inbound.model.request.InboundCreateModel
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import com.smartfoodnet.fninventory.inbound.model.vo.InboundStatusType
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class InboundService(
    val inboundRepository: InboundRepository,
    val basicProductRepository: BasicProductRepository,
    val nosnosClientService: NosnosClientService
) : Log {

    @Transactional
    fun createInbound(createModel: InboundCreateModel) : CreateInboundResponse {
        val inbound = createModel.toEntity()
        createModel.expectedList.forEach {
            val basicProduct = getBasicProduct(it.basicProductId)
            inbound.addExptecdItem(it.toEntity(basicProduct))
        }

        nosnosClientService.sendInboundAndSetRegisterNo(inbound)

        val response = inboundRepository.save(inbound)
        return CreateInboundResponse(response.id!!, response.partnerId, response.createdAt)
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

    @Transactional
    fun cancelInbound(inboundId : Long){
        val inbound = inboundRepository.findById(inboundId).get()

        inbound.status.isCancelPossiible()

        val receivingPlanId = inbound.registrationId?: throw BaseRuntimeException(errorMessage = "입고예정등록번호를 찾을 수 없습니다")

        nosnosClientService.cancelInbound(inbound.partnerId, receivingPlanId)

        inbound.status = InboundStatusType.CANCEL
    }
}