package com.smartfoodnet.fninventory.stock

import com.smartfoodnet.common.model.response.CommonResponse
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fninventory.stock.model.BasicProductStockModel
import com.smartfoodnet.fninventory.stock.model.DailyStockSummaryModel
import com.smartfoodnet.fninventory.stock.model.StockByBestBeforeModel
import com.smartfoodnet.fninventory.stock.support.BasicProductStockSearchCondition
import com.smartfoodnet.fninventory.stock.support.DailyStockSummarySearchCondition
import com.smartfoodnet.fninventory.stock.support.StockByBestBeforeSearchCondition
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
    private val stockService: StockService,
    private val stockScheduledService: StockScheduledService
) {
    @Operation(summary = "특정 화주(고객사) ID 의 상품별 재고 리스트 조회")
    @GetMapping("basic-product/{partnerId}")
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

    @Operation(summary = "특정 화주(고객사) ID 의 상미기한별 재고 리스트 조회")
    @GetMapping("best-before/{partnerId}")
    fun getStocksByBestBefore(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @Parameter(description = "검색조건")
        @ModelAttribute condition: StockByBestBeforeSearchCondition,
        @PageableDefault(size = 50, sort = ["bestBefore"], direction = Sort.Direction.DESC) page: Pageable,
    ): PageResponse<StockByBestBeforeModel> {
        condition.apply { this.partnerId = partnerId }
        return stockService.getStocksByBestBefore(partnerId, condition, page)
    }

    @Operation(summary = "특정 상품별 최근 일주일 재고 이동 내역 리스트")
    @GetMapping("summary/{partnerId}")
    fun getDailyStockSummaries(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @ModelAttribute condition: DailyStockSummarySearchCondition,
        @PageableDefault(size = 20, sort = ["id"], direction = Sort.Direction.DESC) page: Pageable,
        ): PageResponse<DailyStockSummaryModel> {
        condition.apply { this.partnerId = partnerId }
        return stockService.getDailyStockSummaries(condition, page)
    }

    @Operation(summary = "상미기한별 재고 배치 작업")
    @GetMapping("best-before/synchronize")
    fun syncStocksByBestBefore(
    ): CommonResponse{
        stockScheduledService.syncStocksByBestBefore()
        return CommonResponse()
    }
}