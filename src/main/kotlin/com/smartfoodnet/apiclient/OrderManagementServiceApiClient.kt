package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.response.StoreInfoModel
import com.smartfoodnet.common.model.response.CommonResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "orderManagementServiceApiClient",
    url = "\${sfn.service.fn-order-management-service}",
)
interface OrderManagementServiceApiClient {
    @GetMapping("/store/partner/{partnerId}")
    fun getStoreInfos(@PathVariable partnerId: Long, @RequestParam type: String? = "ALL"): CommonResponse<List<StoreInfoModel>>
}