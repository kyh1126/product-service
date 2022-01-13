package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.request.PreSalesProductModel
import com.smartfoodnet.apiclient.request.PreShippingProductModel
import com.smartfoodnet.apiclient.response.*
import io.swagger.annotations.ApiModelProperty
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
        @PathVariable processDate : String
    ): CommonResponse<CommonDataListModel<NosnosStockMoveEventModel>>

    @GetMapping("/stock/daily")
    fun getDailyCloseStock(
        @SpringQueryMap stockDefaultModel: StockDefaultModel,
        @PathVariable processDate : String
    ): CommonResponse<CommonDataListModel<NosnosStockMoveEventModel>>

    @PostMapping("shipping/products")
    fun createShippingProduct(preModel: PreShippingProductModel): CommonResponse<PostShippingProductModel>

    @PutMapping("shipping/products/{shippingProductId}")
    fun updateShippingProduct(
        @PathVariable shippingProductId: Long,
        preModel: PreShippingProductModel
    )

    @PutMapping("sales/products/{salesProductId}")
    fun updateSalesProduct(@PathVariable salesProductId: Long, preModel: PreSalesProductModel)
}

data class CommonResponse<T>(
    val payload: T? = null,
)

data class StockDefaultModel (
    @ApiModelProperty(value="고객사 ID", example = "77", required = true)
    val memberId : Long,
    @ApiModelProperty(value="출고상품 ID")
    val shippingProductIds : List<Int>? = null,
    @ApiModelProperty(value="페이지 번호", example = "1")
    val page : Int = 1
)

data class DailyCloseStockRequestModel(
    val memberId : Long,
    val closingDate : String,
    val shippingProductIds : List<Int>? = null,
    val page: Int? = 1
)