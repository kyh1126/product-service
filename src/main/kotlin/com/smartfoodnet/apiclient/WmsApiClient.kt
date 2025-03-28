package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.request.*
import com.smartfoodnet.apiclient.response.*
import com.smartfoodnet.common.Constants.NOSNOS_INITIAL_PAGE
import com.smartfoodnet.common.model.response.CommonResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.SpringQueryMap
import org.springframework.web.bind.annotation.*

@FeignClient(
    name = "wmsApiClient",
    url = "\${sfn.service.fn-warehouse-management-service}"
)
interface WmsApiClient {
    // ---------------------------------------------------------------------------------------------
    // -- 재고관리
    // ---------------------------------------------------------------------------------------------
    @GetMapping("stock")
    fun getStocks(
        @RequestParam partnerId: Long,
        @RequestParam shippingProductIds: List<Long?>?
    ): CommonResponse<CommonDataListModel<NosnosStockModel>>

    @GetMapping("/stock/expire")
    fun getStocksByExpirationDate(
        @RequestParam partnerId: Long,
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

    // ---------------------------------------------------------------------------------------------
    // -- 출고상품(=기본상품)관리
    // ---------------------------------------------------------------------------------------------
    @GetMapping("shipping/products/bulk")
    fun getShippingProducts(@SpringQueryMap basicProductReadModel: BasicProductReadModel): CommonResponse<CommonDataListModel<NosnosShippingProductModel>>

    @PostMapping("shipping/products")
    fun createShippingProduct(preModel: PreShippingProductModel): CommonResponse<PostShippingProductModel>

    @PutMapping("shipping/products/{shippingProductId}")
    fun updateShippingProduct(
        @PathVariable shippingProductId: Long,
        preModel: PreShippingProductModel
    )

    @PutMapping("shipping/products/bulk")
    fun updateShippingProducts(
        preModel: CommonCreateBulkModel<PreShippingProductSimpleModel>
    )

    // ---------------------------------------------------------------------------------------------
    // -- 출고
    // ---------------------------------------------------------------------------------------------
    @GetMapping("release/bulk")
    fun getReleases(
        @RequestParam(required = false) partnerId: Long? = null,
        @RequestParam(required = false) releaseIds: List<Long>? = null,
        @RequestParam(required = false) orderIds: List<Long>? = null,
        @RequestParam(required = false) shippingOrderInfoId: Int? = null,
        @RequestParam(required = false) releaseDate: String? = null,
        @RequestParam(required = false) requestShippingDt: String? = null,
        @RequestParam page: Int = NOSNOS_INITIAL_PAGE
    ): CommonResponse<CommonDataListModel<NosnosReleaseModel>>

    @GetMapping("release/items")
    fun getReleaseItems(
        @RequestParam releaseIds: List<Long>,
        @RequestParam(required = false) shippingOrderInfoId: Int? = null,
        @RequestParam page: Int = NOSNOS_INITIAL_PAGE
    ): CommonResponse<CommonDataListModel<NosnosReleaseItemModel>>

    @PatchMapping("release/cancel/{releaseId}")
    fun cancelRelease(@PathVariable releaseId: Long)

    // ---------------------------------------------------------------------------------------------
    // -- 판매상품 관리
    // ---------------------------------------------------------------------------------------------
    @PostMapping("sales/products/bulk")
    fun createSalesProducts(
        @RequestBody preModel: CommonCreateBulkModel<PreSalesProductModel>
    ): CommonResponse<CommonProcessBulkModel<NosnosSalesProductModel>>

    @PutMapping("sales/products/{salesProductId}")
    fun updateSalesProduct(@PathVariable salesProductId: Long, preModel: PreSalesProductModel)

    // ---------------------------------------------------------------------------------------------
    // -- 상품연결 정보
    // ---------------------------------------------------------------------------------------------
    @PostMapping("product-mappings/bulk")
    fun createProductMappings(
        @RequestBody preModel: CommonCreateBulkModel<PreProductMappingModel>
    )

    // ---------------------------------------------------------------------------------------------
    // -- 입/출고관리
    // ---------------------------------------------------------------------------------------------
    @GetMapping("inventory/inbounds/work")
    fun getInboundWork(
        @SpringQueryMap inboundWorkReadModel: InboundWorkReadModel
    ): CommonResponse<CommonDataListModel<GetInboundWorkModel>>

    @PostMapping("inventory/inbounds")
    fun createInbound(
        @RequestBody nosNosInboundCreateModel: NosnosInboundCreateModel
    ): CommonResponse<NosnosPostInboundModel>

    @GetMapping("inventory/inbounds/{receivingPlanId}")
    fun getInbound(
        @PathVariable receivingPlanId: Long
    ): CommonResponse<GetInboundModel>

    @PutMapping("inventory/inbounds/{receivingPlanId}/cancel")
    fun cancelInbound(
        @RequestParam partnerId: Long,
        @PathVariable receivingPlanId: Long
    ): CommonResponse<Unit>

    // ---------------------------------------------------------------------------------------------
    // -- 발주
    // ---------------------------------------------------------------------------------------------
    @PostMapping("inventory/outbound")
    fun createOutbound(
        @RequestBody outboundCreateModel: OutboundCreateModel
    ): CommonResponse<PostOutboundModel>

    @PostMapping("inventory/outbounds")
    fun createOutbounds(
        @RequestBody outboundCreateBulkModel: OutboundCreateBulkModel
    ): CommonResponse<CommonProcessBulkModel<PostOutboundModel>>

    @GetMapping("inventory/outbound/cancels/bulk")
    fun getCancelOutbounds(
        @SpringQueryMap outboundCancelReadModel: OutboundCancelReadModel
    ): CommonResponse<CommonDataListModel<GetOutboundCancelModel>>

    @PostMapping("inventory/outbound/cancels")
    fun cancelOutbound(
        @RequestBody outboundCancelCreateModel: OutboundCancelCreateModel
    )

    // ---------------------------------------------------------------------------------------------
    // -- 반품
    // ---------------------------------------------------------------------------------------------
    @PostMapping("return")
    fun createReleaseReturn(
        @RequestBody returnCreateModel: ReturnCreateModel
    ): CommonResponse<PostReturnModel>?

    @GetMapping("return/{releaseReturnInfoId}")
    fun getReleaseReturn(
        @PathVariable releaseReturnInfoId: Long
    ): CommonResponse<GetReturnModel>

    @PatchMapping("return/{releaseReturnInfoId}")
    fun cancelReleaseReturn(
        @PathVariable releaseReturnInfoId: Long
    )

    // ---------------------------------------------------------------------------------------------
    // -- 로케이션,택배
    // ---------------------------------------------------------------------------------------------
    @GetMapping("delivery/agency/info/bulk")
    fun getDeliveryAgencyInfoList(): CommonResponse<CommonDataListModel<NosnosDeliveryAgencyInfoModel>>

    @GetMapping("delivery/cj/status/bulk")
    fun getCjDeliveryStatuses(
        @RequestParam shippingCodes: List<String>? = null
    ): CommonResponse<CommonDataListModel<CjDeliveryStatusModel>>

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


