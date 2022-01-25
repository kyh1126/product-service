package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.request.InboundWorkReadModel
import com.smartfoodnet.apiclient.request.PreSalesProductModel
import com.smartfoodnet.apiclient.request.PreShippingProductModel
import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.GetInboundWorkModel
import com.smartfoodnet.apiclient.response.NosnosDailyCloseStockModel
import com.smartfoodnet.apiclient.response.NosnosDailyStockSummaryModel
import com.smartfoodnet.apiclient.response.NosnosExpirationDateStockModel
import com.smartfoodnet.apiclient.response.NosnosStockModel
import com.smartfoodnet.apiclient.response.NosnosStockMoveEventModel
import com.smartfoodnet.apiclient.response.PostShippingProductModel
import com.smartfoodnet.common.model.response.CommonResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.SpringQueryMap
import org.springframework.web.bind.annotation.*

@FeignClient(
    name = "wmsApiClient",
    url = "\${sfn.service.fn-warehouse-management-service}"
)
interface WmsApiClient {
    @GetMapping("stock")
    fun getStocks(
        @RequestParam(name = "memberId") partnerId: Long,
        @RequestParam shippingProductIds: List<Long?>?
    ): CommonResponse<CommonDataListModel<NosnosStockModel>>

    @GetMapping("/stock/expire")
    fun getStocksByExpirationDate(
        @RequestParam(name = "memberId") partnerId: Long,
        @RequestParam shippingProductIds: List<Long?>?
    ): CommonResponse<CommonDataListModel<NosnosExpirationDateStockModel>>

    @GetMapping("/stock/history/{processDate}")
    fun getStocksMoveEvents(
        @SpringQueryMap stockDefaultModel: StockDefaultModel,
        @PathVariable processDate: String
    ): CommonResponse<CommonDataListModel<NosnosStockMoveEventModel>>

    @GetMapping("/stock/daily")
    fun getDailyCloseStock(
        @SpringQueryMap dailyCloseStockRequestModel: DailyCloseStockRequestModel,
    ): CommonResponse<CommonDataListModel<NosnosDailyCloseStockModel>>

    @GetMapping("/stock/summary")
    fun getDailyStockSummary(
        @SpringQueryMap dailySummaryStockRequestModel: DailySummaryStockRequestModel,
    ): CommonResponse<CommonDataListModel<NosnosDailyStockSummaryModel>>

    @PostMapping("shipping/products")
    fun createShippingProduct(preModel: PreShippingProductModel): CommonResponse<PostShippingProductModel>

    @PutMapping("shipping/products/{shippingProductId}")
    fun updateShippingProduct(
        @PathVariable shippingProductId: Long,
        preModel: PreShippingProductModel
    )

    @PutMapping("sales/products/{salesProductId}")
    fun updateSalesProduct(@PathVariable salesProductId: Long, preModel: PreSalesProductModel)

    @GetMapping("inventory/inbounds/work")
    fun getInboundWork(
        @SpringQueryMap inboundWorkReadModel: InboundWorkReadModel
    ): CommonResponse<CommonDataListModel<GetInboundWorkModel>>
}

data class StockDefaultModel(
    val memberId: Long,
    val shippingProductIds: List<Int>? = null,
    val page: Int = 1
)

data class DailyCloseStockRequestModel(
    val memberId: Long,
    val closingDate: String,
    val shippingProductIds: List<Long>? = null,
    val page: Int? = 1
)

data class DailySummaryStockRequestModel(
    val memberId: Long,
    val stockDate: String,
    val shippingProductIds: List<Long>? = null,
    val page: Int? = 1
)


