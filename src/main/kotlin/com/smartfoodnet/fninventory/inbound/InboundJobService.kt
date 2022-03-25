package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.apiclient.response.GetInboundWorkModel
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fninventory.inbound.model.vo.InboundStatusType
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class InboundJobService(
    val nosnosClientService: NosnosClientService,
    val inboundUnplannedRepository: InboundUnplannedRepository,
    val inboundActualDetailRepository: InboundActualDetailRepository,
    val inboundRepository: InboundRepository,
    val basicProductRepository : BasicProductRepository
) : Log{

    @Transactional
    fun inboundWorkJob(partnerId: Long, basicDt: String){
        // 최초 가져오기 및 데이터 처리
        val firstList = nosnosClientService.getInboundWorkJob(partnerId, basicDt, 1)
        inboundWorkProcess(partnerId, firstList.dataList)

        // 이후 데이터가 있는지 확인
        val totalPage = firstList.totalPage.toInt()

        (2..totalPage).forEach {
            val afterList = nosnosClientService.getInboundWorkJob(partnerId, basicDt, it)
            inboundWorkProcess(partnerId, afterList.dataList)
        }
    }

    private fun inboundWorkProcess(partnerId: Long, dataList: List<GetInboundWorkModel>){
        expectedListProcess(partnerId, dataList.filter { it.receivingType == 1 })
        etcListProcess(partnerId, dataList.filter { it.receivingType != 1 })
    }

    private fun expectedListProcess(partnerId: Long, expectedList : List<GetInboundWorkModel>){
        expectedList.forEach {
            val historyId = it.receivingWorkHistoryId
            if (!inboundActualDetailRepository.existsByReceivingWorkHistoryId(historyId)) {
                val receivingPlanId: Long = it.receivingPlanId!!
                val inboundExpectedDetail = inboundRepository.findInboundExpectedWithBasicProduct(
                    receivingPlanId,
                    it.shippingProductId
                )
                inboundExpectedDetail?.inboundActualDetail?.add(
                    it.toEntity(
                        inboundExpectedDetail,
                        inboundExpectedDetail.basicProduct
                    )
                )
            }
        }
    }

    private fun etcListProcess(partnerId: Long, etcList: List<GetInboundWorkModel>){
        etcList.forEach {
            if (!inboundUnplannedRepository.existsByReceivingWorkHistoryId(it.receivingWorkHistoryId)) {
                val basicProduct = basicProductRepository.findByShippingProductId(it.shippingProductId)
                inboundUnplannedRepository.save(it.toUnplannedEntity(partnerId, basicProduct))
            } else {
                log.warn("duplicate data -> {}", it)
            }

        }
    }

    @Transactional
    fun inboundProcessCheck(basicDt: String) {
        val paramDate = LocalDate.parse(basicDt, DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay()
        val expectedList = inboundRepository.findStatusExpectedInbounds(paramDate)

        expectedList.forEach {
            val responseInboundModel = nosnosClientService.getInbound(it.registrationId!!)
            if (responseInboundModel != null && responseInboundModel.plan_status != 2L) {
                it.status = InboundStatusType.getInboundStatusType(responseInboundModel.plan_status.toInt())
            }
        }
    }
}