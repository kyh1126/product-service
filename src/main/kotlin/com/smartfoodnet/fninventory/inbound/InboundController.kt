package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.GetInboundWorkModel
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fninventory.inbound.model.dto.CreateInboundResponse
import com.smartfoodnet.fninventory.inbound.model.dto.GetInbound
import com.smartfoodnet.fninventory.inbound.model.request.InboundCreateModel
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import com.smartfoodnet.fninventory.inbound.model.request.InboundUnplannedSearchCondition
import com.smartfoodnet.fninventory.inbound.model.response.InboundUnplannedModel
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api(description = "입고관리")
@RestController
@RequestMapping("inbounds")
class InboundController(
    val inboundService: InboundService,
    val nosnosClientService: NosnosClientService,
    val inboundJobService: InboundJobService,
    val inboundUnplannedService: InboundUnplannedService
){

    @PostMapping
    @Operation(summary = "입고 등록")
    fun createInbound(@RequestBody @Valid inboundCreateModel: InboundCreateModel): CreateInboundResponse {
        return inboundService.createInbound(inboundCreateModel)
    }

    @PutMapping("{inboundId}/cancel")
    @Operation(summary = "입고예정 취소")
    fun cancelInbound(
        @RequestParam partnerId: Long,
        @PathVariable inboundId : Long
    ){
        inboundService.cancelInbound(partnerId, inboundId)
    }

    @GetMapping("partner/{partnerId}")
    @Operation(summary = "입고예정 조회")
    fun getInbound(
        @Parameter(description = "고객사 ID", example = "14") @PathVariable partnerId : Long,
        @ModelAttribute condition: InboundSearchCondition,
        @PageableDefault(size = 50, sort = ["id"], direction = Sort.Direction.DESC) page: Pageable
    ): PageResponse<GetInbound>? {
        return inboundService.getInbound(condition.apply { this.partnerId = partnerId }, page)
    }

    @GetMapping("partner/{partnerId}/unplanned")
    @Operation(summary = "미예정 입고조회")
    fun getInboundUnplanned(
        @Parameter(description = "고객사 ID", example = "14") @PathVariable partnerId : Long,
        @ModelAttribute condition: InboundUnplannedSearchCondition,
        @PageableDefault(size = 50) page: Pageable
    ): PageResponse<InboundUnplannedModel> =
        inboundUnplannedService.getInboundUnplanned(condition.apply { this.partnerId = partnerId }, page)

    @GetMapping("partner/{partnerId}/details/{expectedDetailId}")
    @Operation(summary = "입고내역 조회")
    fun getInboundDetail(
        @Parameter(description = "고객사 ID", example = "14") @PathVariable partnerId: Long,
        @Parameter(description = "입고예정 상세번호", example = "3") @PathVariable expectedDetailId: Long
    ) = inboundService.getInboundActualDetail(partnerId, expectedDetailId)

    @GetMapping("partner/{partnerId}/work")
    @Operation(summary = "입고작업 내역 조회")
    fun getInboundWork(
        @PathVariable partnerId: Long,
        @RequestParam startDt : String,
        @RequestParam endDt : String,
        @RequestParam(defaultValue = "1") page : Int
    ) : CommonDataListModel<GetInboundWorkModel>? {
        return nosnosClientService.getInboundWork(partnerId, startDt, endDt, page)
    }

    @GetMapping("partner/{partnerId}/work/job")
    @Operation(summary = "입고작업 내역(배치용)")
    fun getInboundWorkSchedule(
        @PathVariable partnerId: Long,
        @RequestParam basicDt : String
    ){
        inboundJobService.inboundWorkJob(partnerId, basicDt)
    }

    @GetMapping("/result/job")
    @Operation(summary = "입고예정상태값 노스노스 조회 (배치용)")
    fun getInboundResult(
        @Parameter(description ="기준일자(yyyyMMdd)", example = "20220126") @RequestParam basicDt: String
    ){
        inboundJobService.inboundProcessCheck(basicDt)
    }
}