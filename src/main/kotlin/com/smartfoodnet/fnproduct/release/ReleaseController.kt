package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.fnproduct.release.model.response.ReleaseInfoDetailModel
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(description = "릴리즈 관련 API")
@RestController
@RequestMapping("/release")
class ReleaseController(
    private val releaseInfoService: ReleaseInfoService
) {
    @Operation(summary = "릴리즈 정보 상세 조회")
    @GetMapping("{id}")
    fun getReleaseInfo(
        @Parameter(description = "릴리즈 정보 ID", required = true)
        @PathVariable id: Long,
    ): ReleaseInfoDetailModel {
        return releaseInfoService.getReleaseInfo(id)
    }
}
