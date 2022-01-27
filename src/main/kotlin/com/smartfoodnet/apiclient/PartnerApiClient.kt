package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.response.PartnerIdPairModel
import com.smartfoodnet.common.model.response.CommonResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "partnerApiClient",
    url = "\${sfn.service.fn-partner}",
    path = "/partner"
)
interface PartnerApiClient {
    @GetMapping("{id}")
    fun getPartner(@PathVariable id: Long): CommonResponse<PartnerIdPairModel>

    @GetMapping("ids")
    fun loadAllPartners(): CommonResponse<List<PartnerIdPairModel>>
}
