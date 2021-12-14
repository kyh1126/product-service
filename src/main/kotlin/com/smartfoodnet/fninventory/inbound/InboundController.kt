package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fninventory.inbound.model.request.InboundCreateModel
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import com.smartfoodnet.fninventory.inbound.model.response.InboundModel
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api(description = "입고관리")
@RestController
@RequestMapping("inbound")
@Validated
class InboundController(
    val inboundService: InboundService,
    val inboundDetailService: InboundDetailService
) {

    @Operation(summary = "입고 등록")
    @PostMapping
    fun createInbound(@RequestBody @Valid list: List<InboundCreateModel>) {
        inboundService.createInbound(list)
    }

    @Operation(summary = "입고 취소")
    @PatchMapping("{inboundId}")
    fun cancelInbound(@PathVariable inboundId: Long) {
        inboundService.cancelInbound(inboundId)
    }

    @Operation(summary = "단건 입고 조회")
    @GetMapping("{inboundId}")
    fun getInbound(@PathVariable inboundId: Long) = inboundService.getInbound(inboundId)

    @Operation(summary = "복수건 입고 조회")
    @GetMapping("partnerId/{partnerId}")
    fun getInbounds(
        @Parameter(description = "고객사 ID", example = "1") @PathVariable partnerId: Long,
        @ModelAttribute condition: InboundSearchCondition,
        @PageableDefault(size = 50, sort = ["id"], direction = Sort.Direction.DESC) page: Pageable
    ): PageResponse<InboundModel> {
        condition.partnerId = partnerId
        return inboundService.getInbounds(condition, page)
    }

    @Operation(summary = "입고 정보 상세")
    @GetMapping("{inboundId}/detail")
    fun getInboundDetails(@PathVariable inboundId: Long) =
        inboundDetailService.getDetail(inboundId)

    companion object : Log
}