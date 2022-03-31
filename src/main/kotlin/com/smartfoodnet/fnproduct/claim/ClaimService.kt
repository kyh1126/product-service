package com.smartfoodnet.fnproduct.claim

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.response.ReturnCreateItem
import com.smartfoodnet.apiclient.response.ReturnCreateModel
import com.smartfoodnet.fnproduct.claim.entity.ReturnProduct
import com.smartfoodnet.fnproduct.claim.model.ClaimCreateModel
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimReason
import com.smartfoodnet.fnproduct.release.ReleaseInfoRepository
import com.smartfoodnet.fnproduct.release.ReleaseInfoService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ClaimService(
    private val wmsApiClient: WmsApiClient,
    private val releaseInfoService: ReleaseInfoService,
    private val releaseInfoRepository: ReleaseInfoRepository
) {
    fun createClaim(claimCreateModel: ClaimCreateModel) {
        val claim = claimCreateModel.toEntity()

        val returnProducts = buildReturnProducts(claimCreateModel)
        claim.returnProducts = returnProducts.toMutableList()


    }

    private fun buildReturnProducts(claimCreateModel: ClaimCreateModel): List<ReturnProduct> {
        return claimCreateModel.returnProducts.map {
            ReturnProduct(
                basicProductId = it.basicProductId,
                shippingProductId = it.shippingProductId,
                quantity = it.quantity
            )
        }
    }

    fun getReturnInfo() {
        val releaseInfo = releaseInfoRepository.findById(1).get()

        val releaseItems = getReleaseItems()

        val returnCreateModel = ReturnCreateModel(
            partnerId = 11,
            releaseId = releaseInfo.releaseId?.toInt(),
            returnReasonId = ClaimReason.CHANGED_MIND.returnReasonId?.toInt(),
            memo = "memo",
            returnAddress1 = "returnAddress1",
            returnAddress2 = "returnAddress2",
            receivingName = "회수 고객명",
            zipcode = "463-859",
            tel1 = "124124124",
            tel2 = "tel2",
            releaseItemList = releaseItems
        )
        val returnModel = wmsApiClient.createReleaseReturn(returnCreateModel)

        println(returnModel)
    }


    private fun getReleaseItems(): List<ReturnCreateItem> {
        val releaseItems = mutableListOf<ReturnCreateItem>()

        releaseItems.add(
            ReturnCreateItem(
                shippingProductId = 1031,
                quantity = 2
            )
        )

        return releaseItems
    }
}