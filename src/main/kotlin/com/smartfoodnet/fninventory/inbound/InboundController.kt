package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fninventory.inbound.model.dto.GetInbound
import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundParent
import com.smartfoodnet.fninventory.inbound.model.request.InboundCreateModel
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
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
    val inboundService: InboundService
){

    @PostMapping
    @Operation(summary = "입고 등록")
    fun createInbound(@RequestBody @Valid inboundCreateModel: InboundCreateModel) {
        inboundService.createInbound(inboundCreateModel)
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

//    @GetMapping("partner/{partnerId}/details/{expectedDetailId}")
//    @Operation(summary = "입고예정 상세내역 조회")
//    fun getInboundDetail(
//        @Parameter(description = "고객사 ID", example = "14") @PathVariable partnerId: Long,
//        @Parameter(description = "입고예정 상세번호", example = "3") @PathVariable expectedDetailId: Long
//    ) = inboundService.getInboundDetail(partnerId, expectedDetailId)




//    @Operation(summary = "입고 취소")
//    @PatchMapping("{inboundId}")
//    fun cancelInbound(@PathVariable inboundId: Long) {
//        inboundService.cancelInbound(inboundId)
//    }
//
//    @Operation(summary = "단건 입고 조회")
//    @GetMapping("{inboundId}")
//    fun getInbound(@PathVariable inboundId: Long) = inboundService.getInbound(inboundId)
//
//    @Operation(summary = "복수건 입고 조회")
//    @GetMapping("partnerId/{partnerId}")
//    fun getInbounds(
//        @Parameter(description = "고객사 ID", example = "1") @PathVariable partnerId: Long,
//        @ModelAttribute condition: InboundSearchCondition,
//        @PageableDefault(size = 50, sort = ["id"], direction = Sort.Direction.DESC) page: Pageable
//    ): PageResponse<InboundModel> {
//        condition.partnerId = partnerId
//        return inboundService.getInbounds(condition, page)
//    }
//
//    @Operation(summary = "입고 정보 상세")
//    @GetMapping("{inboundId}/detail")
//    fun getInboundDetails(@PathVariable inboundId: Long) =
//        inboundDetailService.getDetail(inboundId)

    companion object : Log
}