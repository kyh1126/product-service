package com.smartfoodnet.apiclient

import com.smartfoodnet.apiclient.response.PartnerIdPairModel
import com.smartfoodnet.apiclient.response.UserPartnerInfoModel
import com.smartfoodnet.common.model.response.CommonResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "partnerApiClient",
    url = "\${sfn.service.fn-partner}",
    path = "/partner"
)
interface PartnerApiClient {
    @GetMapping("")
    fun getUserPartnerInfo(@RequestParam userId: Long): CommonResponse<UserPartnerInfoModel>

    @GetMapping("{id}")
    fun getPartner(@PathVariable id: Long): CommonResponse<PartnerIdPairModel>

    @GetMapping("ids")
    fun loadAllPartners(): CommonResponse<List<PartnerIdPairModel>>
}
