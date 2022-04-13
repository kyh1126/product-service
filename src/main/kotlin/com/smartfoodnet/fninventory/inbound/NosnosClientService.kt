package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.request.InboundWorkReadModel
import com.smartfoodnet.apiclient.request.NosnosInboundCreateModel
import com.smartfoodnet.apiclient.request.PlanProduct
import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.GetInboundModel
import com.smartfoodnet.apiclient.response.GetInboundWorkModel
import com.smartfoodnet.fninventory.inbound.entity.Inbound
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
class NosnosClientService(
    val wmsApiClient: WmsApiClient
) {
    fun getInboundWork(partnerId: Long, startDt: String, endDt: String, page: Int): CommonDataListModel<GetInboundWorkModel>? {
        val params = InboundWorkReadModel(
            memberId = partnerId,
            startDt = startDt,
            endDt = endDt,
            page = page
        )

        return wmsApiClient.getInboundWork(params).payload
    }

    fun getInboundWorkJob(partnerId: Long, basicDt: String, page: Int): CommonDataListModel<GetInboundWorkModel> {
        val params = InboundWorkReadModel(
            memberId = partnerId,
            startDt = basicDt,
            endDt = basicDt,
            page = page
        )
        return wmsApiClient.getInboundWork(params).payload!!
    }

    fun getInbound(receivingPlanId: Long): GetInboundModel? {
        return wmsApiClient.getInbound(receivingPlanId).payload
    }

    fun cancelInbound(partnerId: Long, receivingPlanId: Long) {
        wmsApiClient.cancelInbound(partnerId, receivingPlanId)
    }

    fun sendInboundAndSetRegisterNo(inbound: Inbound) {
        val planProductList: List<PlanProduct> = inbound.expectedList.map {
            PlanProduct(it.basicProduct.shippingProductId!!, it.requestQuantity)
        }

        val trackingNumbers = inbound.expectedList.mapNotNull {
            if (it.trackingNumber.isNullOrBlank()) null else it.trackingNumber
        }.joinToString { it }

        val nosnosInboundCreateModel = NosnosInboundCreateModel(
            memberId = inbound.partnerId,
            planDate = inbound.expectedDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
            memo = trackingNumbers,
            planProductList = planProductList
        )

        val response = wmsApiClient.createInbound(nosnosInboundCreateModel).payload

        if (response != null) {
            inbound.registrationId = response.receivingPlanId
            inbound.registrationNo = response.receivingPlanCode
        } else {
            throw RuntimeException("NOSNOS 입고예정 등록에 실패하였습니다")
        }
    }
}
