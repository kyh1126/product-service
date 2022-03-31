package com.smartfoodnet.fninventory.shortage

import com.smartfoodnet.fninventory.shortage.model.ProductShortageModel
import com.smartfoodnet.fninventory.shortage.support.ProductShortageSearchCondition
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.*

@Api(description = "결품 관련 API")
@RestController
@RequestMapping("shortage")
class ShortageController(
    private val shortageService: ShortageService
) {
    @Operation(summary = "특정 화주(고객사) ID 의 결품현황 리스트 조회")
    @GetMapping("partners/{partnerId}")
    fun getProductShortages(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @Parameter(description = "검색조건")
        @ModelAttribute condition: ProductShortageSearchCondition,
    ): List<ProductShortageModel>? {
        condition.apply { this.partnerId = partnerId }
        return shortageService.getProductShortages(partnerId, condition)
    }
}