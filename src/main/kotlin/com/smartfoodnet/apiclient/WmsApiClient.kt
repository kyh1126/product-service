package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.request.InboundWorkReadModel
import com.smartfoodnet.apiclient.request.NosnosInboundCreateModel
import com.smartfoodnet.apiclient.request.PreSalesProductModel
import com.smartfoodnet.apiclient.request.PreShippingProductModel
import com.smartfoodnet.apiclient.response.*
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
        @SpringQueryMap inboundWorkReadModel : InboundWorkReadModel
    ) : CommonResponse<CommonDataListModel<GetInboundWorkModel>>

    @PostMapping("inventory/inbounds")
    fun createInbound(
        @RequestBody nosNosInboundCreateModel: NosnosInboundCreateModel
    ) : CommonResponse<NosnosPostInboundModel>
}
