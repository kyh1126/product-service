package com.smartfoodnet.fnproduct.claim

import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.claim.model.ClaimCreateModel
import com.smartfoodnet.fnproduct.claim.model.ClaimModel
import com.smartfoodnet.fnproduct.claim.support.condition.ClaimSearchCondition
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.parameters.RequestBody
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(description = "클레임 관련 API")
@RestController
@RequestMapping("/claim")
class ClaimController(
    private val claimService: ClaimService
) {
    @Operation(summary = "반품 현황 조회")
    @GetMapping
    fun getClaims(
        @Parameter(description = "검색조건")
        @ModelAttribute condition: ClaimSearchCondition,
        @PageableDefault(size = 50, sort = ["id"], direction = Sort.Direction.DESC) page: Pageable,
    ): PageResponse<ClaimModel>{
        return PageResponse.of(claimService.findClaims(condition, page))
    }

    @Operation(summary = "클레임 등록")
    @PostMapping
    fun createClaim(
        @RequestBody claimCreateModel: ClaimCreateModel
    ){
        return claimService.createClaim(claimCreateModel)
    }
}