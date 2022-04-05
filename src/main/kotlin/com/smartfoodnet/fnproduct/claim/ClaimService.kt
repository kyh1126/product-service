package com.smartfoodnet.fnproduct.claim

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.response.ReturnCreateItem
import com.smartfoodnet.apiclient.response.ReturnCreateModel
import com.smartfoodnet.common.error.exception.NoSuchElementError
import com.smartfoodnet.fnproduct.claim.entity.Claim
import com.smartfoodnet.fnproduct.claim.entity.ReturnProduct
import com.smartfoodnet.fnproduct.claim.model.ClaimCreateModel
import com.smartfoodnet.fnproduct.claim.model.ClaimModel
import com.smartfoodnet.fnproduct.claim.support.ClaimRepository
import com.smartfoodnet.fnproduct.claim.support.condition.ClaimSearchCondition
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.release.ReleaseInfoRepository
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ClaimService(
    private val wmsApiClient: WmsApiClient,
    private val releaseInfoRepository: ReleaseInfoRepository,
    private val basicProductRepository: BasicProductRepository,
    private val claimRepository: ClaimRepository
) {
    fun findClaims(
        condition: ClaimSearchCondition,
        page: Pageable
    ): Page<ClaimModel> {
        return claimRepository.findAll(condition.toPredicate(), page).map { ClaimModel.from(it) }
    }

    fun createClaim(claimCreateModel: ClaimCreateModel) {
        val claim = claimCreateModel.toEntity()
        claim.returnProducts = buildReturnProducts(claimCreateModel, claim).toMutableList()
        val releaseInfo = releaseInfoRepository.findByIdOrNull(claimCreateModel.releaseInfoId) ?: throw NoSuchElementError("releaseInfo가 존재하지 않습니다.")

        sendReleaseReturn(claim, releaseInfo)

        claimRepository.save(claim)
    }

    private fun buildReturnProducts(claimCreateModel: ClaimCreateModel, claim: Claim): List<ReturnProduct> {
        return claimCreateModel.returnProducts.map {
            val basicProduct = basicProductRepository.findByShippingProductId(it.shippingProductId) ?: throw NoSuchElementError("기본상품이 존재하지 않습니다. [shippingProductId: ${it.shippingProductId}]")
            ReturnProduct(
                requestQuantity = it.quantity,
                claim = claim,
                basicProduct = basicProduct,
                receiver = claim.releaseInfo.confirmOrder?.receiver ?: throw NoSuchElementError("수신자 정보가 없습니다. [releaseInfoId:${claim.releaseInfo.id}]")
            )
        }
    }

    fun sendReleaseReturn(claim: Claim, releaseInfo: ReleaseInfo) {
        val releaseItems = claim.returnProducts.map {
            ReturnCreateItem(shippingProductId = it.basicProduct.shippingProductId!!, quantity = it.requestQuantity)
        }

        val returnCreateModel = ReturnCreateModel(
            partnerId = claim.partnerId,
            releaseId = releaseInfo.releaseId?.toInt(),
            returnReasonId = claim.claimReason.returnReasonId?.toInt(),
            memo = claim.memo,
            returnAddress1 = releaseInfo.confirmOrder?.receiver?.address,
            receivingName = releaseInfo.confirmOrder?.receiver?.name,
            zipcode = releaseInfo.confirmOrder?.receiver?.zipCode,
            tel1 = releaseInfo.confirmOrder?.receiver?.phoneNumber,
            releaseItemList = releaseItems
        )
        val returnModel = wmsApiClient.createReleaseReturn(returnCreateModel)

        println(returnModel)
    }
}