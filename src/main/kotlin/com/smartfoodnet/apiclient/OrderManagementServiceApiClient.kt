package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.request.TrackingOptionModel
import com.smartfoodnet.apiclient.response.StoreInfoModel
import com.smartfoodnet.common.model.response.CommonResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(
    name = "orderManagementServiceApiClient",
    url = "\${sfn.service.fn-order-management-service}",
)
interface OrderManagementServiceApiClient {
    @GetMapping("/store/partner/{partnerId}")
    fun getStoreInfos(@PathVariable partnerId: Long, @RequestParam type: String? = "ALL"): CommonResponse<List<StoreInfoModel>>

    @PostMapping("order/tracking/{partnerId}/{storeCode}")
    fun sendTrackingNumber(
        @PathVariable partnerId: Long,
        @PathVariable storeCode: String,
        @RequestBody option: TrackingOptionModel
    )
}
