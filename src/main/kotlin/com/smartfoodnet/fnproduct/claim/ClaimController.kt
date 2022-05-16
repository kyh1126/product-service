package com.smartfoodnet.fnproduct.claim

import com.smartfoodnet.common.model.response.CommonResponse
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.claim.model.ClaimCancelModel
import com.smartfoodnet.fnproduct.claim.model.ClaimCreateModel
import com.smartfoodnet.fnproduct.claim.model.ClaimModel
import com.smartfoodnet.fnproduct.claim.model.ExchangeReleaseCreateModel
import com.smartfoodnet.fnproduct.claim.support.condition.ClaimSearchCondition
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Api(description = "클레임 관련 API")
@RestController
class ClaimController(
    private val claimService: ClaimService
) {
    @Operation(summary = "반품 현황 조회")
    @GetMapping("claim/partner/{partnerId}")
    fun findClaims(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @Parameter(description = "검색조건")
        @ModelAttribute condition: ClaimSearchCondition,
        @PageableDefault(size = 50, sort = ["id"], direction = Sort.Direction.DESC) page: Pageable,
    ): PageResponse<ClaimModel> {
        condition.apply {
            this.partnerId = partnerId
        }
        return PageResponse.of(claimService.findClaims(condition, page))
    }

    @Operation(summary = "반품정보 동기화")
    @PostMapping("claim:sync")
    fun syncClaims(): CommonResponse<String> {
        claimService.syncReturnInfos()
        return CommonResponse(HttpStatus.OK.reasonPhrase)
    }

    @Operation(summary = "클레임 등록")
    @PostMapping("claim")
    fun createClaim(
        @RequestBody claimCreateModel: ClaimCreateModel
    ): ClaimModel {
        return claimService.createClaim(claimCreateModel)
    }

    @Operation(summary = "클레임 취소")
    @PostMapping("claim:cancel/{claimId}")
    fun cancelClaim(
        @PathVariable claimId: Long
    ): CommonResponse<String> {
        claimService.cancelClaim(claimId)
        return CommonResponse(HttpStatus.OK.reasonPhrase)
    }

    @Operation(summary = "교환출고 등록")
    @PostMapping("exchangeRelease")
    fun createExchangeRelease(
        @RequestBody exchangeReleaseCreateModel: ExchangeReleaseCreateModel
    ) {
        claimService.createExchangeRelease(exchangeReleaseCreateModel)
    }
}