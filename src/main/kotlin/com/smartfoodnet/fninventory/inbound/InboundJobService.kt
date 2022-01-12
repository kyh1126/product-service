package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.apiclient.WmsClient
import com.smartfoodnet.apiclient.request.InboundWorkReadModel
import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.GetInboundWorkModel
import com.smartfoodnet.common.utils.Log
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InboundJobService(
    val wmsClient: WmsClient,
    val inboundUnplannedRepository: InboundUnplannedRepository,
    val inboundRepository: InboundRepository
) : Log{

    @Transactional
    fun inboundWorkJob(partnerId: Long, basicDt: String){
        // 최초 가져오기 및 데이터 처리
        val firstList = getInboundWorkJob(partnerId, basicDt, 1)
        inboundWorkProcess(partnerId, firstList.dataList)

        // 이후 데이터가 있는지 확인
        val totalPage = firstList.totalPage

        (2..totalPage).forEach {
            val afterList = getInboundWorkJob(partnerId, basicDt, it.toInt())
            inboundWorkProcess(partnerId, afterList.dataList)
        }
    }

    private fun getInboundWorkJob(partnerId: Long, basicDt: String, page: Int): CommonDataListModel<GetInboundWorkModel> {
        // TODO : 스케줄러로 돌아가는 로직 구현
        val params = InboundWorkReadModel(
            memberId = partnerId,
            startDt = basicDt,
            endDt = basicDt,
            page = page
        )
        return wmsClient.getInboundWork(params).payload!!
    }

    private fun inboundWorkProcess(partnerId: Long, dataList: List<GetInboundWorkModel>){
        // TODO : 입고예정검수와 그 외 상태들을 분리하여 구현
        expectedListProcess(partnerId, dataList.filter { it.receiving_type == 1 })
        etcListProcess(partnerId, dataList.filter { it.receiving_type != 1 })
    }

    private fun expectedListProcess(partnerId: Long, expectedList : List<GetInboundWorkModel>){
        log.info("expectedList -> {}", expectedList.size)
        // TODO : 입고예정 등록번호 확인하여 실 입고 처리를 해야함
        expectedList.forEach {
            val receivingPlanId : Long = it.receiving_plan_id!!
            val inboundExpectedDetail = inboundRepository.findInboundExpectedWithBasicProduct(receivingPlanId, it.shipping_product_id)

            inboundExpectedDetail?.inboundActualDetail?.add(it.toEntity(inboundExpectedDetail, inboundExpectedDetail.basicProduct!!))

            log.info("receiving_plan_id -> {}, expire_date -> {}, quantity -> {}\twork_type-> {}", it.receiving_plan_id, it.expire_date, it.quantity, it.work_type)
        }
    }

    private fun etcListProcess(partnerId: Long, etcList: List<GetInboundWorkModel>){
        log.info("etcList -> {}", etcList.size)
        // TODO : 미예정 입고 처리
        etcList.forEach {
            log.info("it -> {}",it)
//            if (!inboundUnplannedRepository.existsByUniqueId(it.uniqueId)){
//                inboundUnplannedRepository.save(InboundUnplanned.toEntity(partnerId, it))
//            }
        }
    }
}