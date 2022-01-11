package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.NosnosExpirationDateStockModel
import com.smartfoodnet.apiclient.response.NosnosStockModel
import com.smartfoodnet.apiclient.response.NosnosStockMoveEventModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.SpringQueryMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "wmsApiClient",
    url = "\${sfn.service.fn-warehouse-management-service}"
)
interface WmsClient {
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