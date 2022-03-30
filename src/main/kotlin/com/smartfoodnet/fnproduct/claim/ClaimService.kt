package com.smartfoodnet.fnproduct.claim

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.response.ReturnCreateModel
import com.smartfoodnet.fnproduct.claim.model.ClaimCreateModel
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimReason
import com.smartfoodnet.fnproduct.release.ReleaseInfoRepository
import com.smartfoodnet.fnproduct.release.ReleaseInfoService
import org.springframework.stereotype.Service

@Service
class ClaimService(
    private val wmsApiClient: WmsApiClient,
    private val releaseInfoService: ReleaseInfoService,
    private val releaseInfoRepository: ReleaseInfoRepository
)  {
    fun createClaim(claimCreateModel: ClaimCreateModel) {

    }

    fun getReturnInfo() {
        val releaseInfo = releaseInfoRepository.findById(1).get()
        val returnCreateModel = ReturnCreateModel(
            partnerId = 46,
            releaseId = releaseInfo.releaseId?.toInt(),
            returnReasonId = ClaimReason.CHANGED_MIND.returnReasonId?.toInt(),
            memo = "memo",
            returnAddress1 = "returnAddress1",
            returnAddress2 = "returnAddress2",
            receivingName = "회수 고객명",
            zipcode = "463-859",
            tel1 = "124124124",
            tel2 = "tel2",
            releaseItemList = null
        )
        val returnModel = wmsApiClient.createReleaseReturn(returnCreateModel)

        println()
    }
}