package com.smartfoodnet.fninventory.stock

import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.order.model.OrderDetailModel
import com.smartfoodnet.fnproduct.order.support.OrderSearchCondition
import com.smartfoodnet.fninventory.stock.model.BasicProductStockModel
import com.smartfoodnet.fninventory.stock.support.BasicProductStockSearchCondition
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*


@Api(description = "재고 관련 API")
@RestController
@RequestMapping("stock")
class StockController(
    private val stockService: StockService
) {
    @Operation(summary = "특정 화주(고객사) ID 의 주문 리스트 조회")
    @GetMapping("partners/{partnerId}")
    fun getBasicProductStocks(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @Parameter(description = "검색조건")
        @ModelAttribute condition: BasicProductStockSearchCondition,
        @PageableDefault(size = 50, sort = ["id"], direction = Sort.Direction.DESC) page: Pageable,
    ): PageResponse<BasicProductStockModel> {
        condition.apply { this.partnerId = partnerId }
        return stockService.getBasicProductStocks(partnerId, condition, page)
    }
}