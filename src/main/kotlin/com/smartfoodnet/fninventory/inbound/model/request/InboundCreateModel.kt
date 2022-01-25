package com.smartfoodnet.fninventory.inbound.model.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.smartfoodnet.apiclient.request.NosnosInboundCreateModel
import com.smartfoodnet.apiclient.request.PlanProduct
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fninventory.inbound.entity.Inbound
import com.smartfoodnet.fninventory.inbound.model.vo.InboundStatusType
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class InboundCreateModel(

    @ApiModelProperty(value = "화주(고객사) ID", example = "14")
    @field:NotNull(message = "화주(고객사) ID를 입력하세요")
    val partnerId: Long,

    @ApiModelProperty(value = "입고예정일자", example = "2021-12-03 13:22:33")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val expectedDate: LocalDateTime,

    @ApiModelProperty(value = "입고예정 기본 상품 리스트")
    @field:Valid
    val expectedList: List<InboundExpectedModel> = listOf()

){
    fun toEntity() = Inbound(
        partnerId = partnerId,
        expectedDate = expectedDate,
        status = InboundStatusType.EXPECTED
    )

    fun toNosNosModel(planProductList : List<PlanProduct>) : NosnosInboundCreateModel {

        val trackingNumbers = expectedList.map { it.trackingNo }.joinToString { it!! }

        return NosnosInboundCreateModel(
            memberId = partnerId,
            planDate = expectedDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
            memo = trackingNumbers,
            planProductList = planProductList
        )
    }

}