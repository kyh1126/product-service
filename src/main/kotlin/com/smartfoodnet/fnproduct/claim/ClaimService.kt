package com.smartfoodnet.fnproduct.claim

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.request.OrderItem
import com.smartfoodnet.apiclient.request.OutboundCreateModel
import com.smartfoodnet.apiclient.response.ReturnCreateModel
import com.smartfoodnet.apiclient.response.ReturnItemCreateModel
import com.smartfoodnet.common.Constants
import com.smartfoodnet.common.error.exception.ExternalApiError
import com.smartfoodnet.common.error.exception.NoSuchElementError
import com.smartfoodnet.fnproduct.claim.entity.Claim
import com.smartfoodnet.fnproduct.claim.entity.ExchangeProduct
import com.smartfoodnet.fnproduct.claim.entity.ExchangeRelease
import com.smartfoodnet.fnproduct.claim.entity.QClaim.claim
import com.smartfoodnet.fnproduct.claim.entity.ReturnInfo
import com.smartfoodnet.fnproduct.claim.entity.ReturnProduct
import com.smartfoodnet.fnproduct.claim.model.ClaimCreateModel
import com.smartfoodnet.fnproduct.claim.model.ClaimModel
import com.smartfoodnet.fnproduct.claim.model.ExchangeReleaseCreateModel
import com.smartfoodnet.fnproduct.claim.support.ClaimRepository
import com.smartfoodnet.fnproduct.claim.support.ExchangeReleaseRepository
import com.smartfoodnet.fnproduct.claim.support.condition.ClaimSearchCondition
import com.smartfoodnet.fnproduct.order.entity.Receiver
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.release.ReleaseInfoRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class ClaimService(
    private val wmsApiClient: WmsApiClient,
    private val releaseInfoRepository: ReleaseInfoRepository,
    private val basicProductRepository: BasicProductRepository,
    private val claimRepository: ClaimRepository,
    private val exchangeReleaseRepository: ExchangeReleaseRepository
) {
    fun findClaims(
        condition: ClaimSearchCondition,
        page: Pageable
    ): Page<ClaimModel> {
        return claimRepository.findAll(condition.toPredicate(), page).map { ClaimModel.from(it) }
    }

    @Transactional
    fun createClaim(claimCreateModel: ClaimCreateModel) {
        val claim = buildClaim(claimCreateModel)

        sendReleaseReturn(claim)

        claimRepository.save(claim)
    }

    @Transactional
    fun createExchangeRelease(exchangeReleaseCreateModel: ExchangeReleaseCreateModel) {
        val claim = claimRepository.findByIdOrNull(exchangeReleaseCreateModel.claimId)
            ?: throw NoSuchElementError("Claim이 존재하지 않습니다: [claimId: ${exchangeReleaseCreateModel.claimId}]")
        val exchangeRelease = ExchangeRelease(
            receiver = exchangeReleaseCreateModel.receiver?.toEntity() ?: claim.returnInfo!!.receiver,
            claim = claim
        )

        exchangeRelease.exchangeProducts.addAll(buildExchangeProducts(exchangeReleaseCreateModel, exchangeRelease))
        claim.exchangeRelease = exchangeRelease

        exchangeReleaseRepository.save(exchangeRelease)

        sendExchangeReleaseOutbound(exchangeRelease)
    }

    fun syncReturnInfoWithNosnos(claim: Claim) {
        val nosnosReturnModel = wmsApiClient.getReleaseReturn(claim.returnInfo?.nosnosReleaseReturnInfoId
            ?: throw NoSuchElementError("반품정보가 존재하지 않습니다. [claimId: ${claim.id}]"))

    }

    fun getNosnosReturnByCoroutine(claims: List<Claim>) {
        val coroutines = mutableListOf<>()
    }

    private fun sendReleaseReturn(claim: Claim) {
        val returnItems = claim.returnInfo?.returnProducts?.map {
            ReturnItemCreateModel(shippingProductId = it.basicProduct.shippingProductId!!, quantity = it.requestQuantity)
        }

        val returnCreateModel = ReturnCreateModel(
            partnerId = claim.partnerId,
            releaseId = claim.releaseInfo?.releaseId?.toInt(),
            returnReasonId = claim.claimReason.returnReasonId?.toInt(),
            memo = claim.memo,
            returnAddress1 = claim.releaseInfo?.confirmOrder?.receiver?.address,
            receivingName = claim.releaseInfo?.confirmOrder?.receiver?.name,
            zipcode = claim.releaseInfo?.confirmOrder?.receiver?.zipCode,
            tel1 = claim.releaseInfo?.confirmOrder?.receiver?.phoneNumber,
            returnItemList = returnItems
        )
        val returnModel = wmsApiClient.createReleaseReturn(returnCreateModel)
        claim.returnInfo?.nosnosReleaseReturnInfoId = returnModel?.releaseReturnInfoId
        claim.returnInfo?.returnCode = returnModel?.returnCode
    }

    private fun sendExchangeReleaseOutbound(exchangeRelease: ExchangeRelease) {
        val outboundCreateModel = buildOutboundCreateModel(exchangeRelease)
        val postOutboundModel = wmsApiClient.createOutbound(outboundCreateModel).payload ?: throw ExternalApiError("Nosnos에 교환발주를 요청하는데 실패하였습니다. [claimId: ${exchangeRelease.claim.id}")

        exchangeRelease.nosnosOrderId = postOutboundModel.orderId
        exchangeRelease.nosnosOrderCode = postOutboundModel.orderCode
    }

    private fun buildOutboundCreateModel(exchangeRelease: ExchangeRelease): OutboundCreateModel {
        val originalConfirmOrder = exchangeRelease.claim.releaseInfo?.confirmOrder ?: throw NoSuchElementError("원 발주정보가 존재하지 않습니다. [releaseInfoId = ${exchangeRelease.claim.releaseInfo?.id}]")

        return OutboundCreateModel(
            partnerId = originalConfirmOrder.partnerId,
            companyOrderCode = originalConfirmOrder.requestOrderList.first().collectedOrder.orderNumber + "-" + exchangeRelease.id,
            shippingMethodId = originalConfirmOrder.shippingMethodType,
            requestShippingDt = originalConfirmOrder.requestShippingDate.format(DateTimeFormatter.ofPattern(Constants.NOSNOS_DATE_FORMAT)),
            buyerName = exchangeRelease.receiver.name,
            receiverName = exchangeRelease.receiver.name,
            tel1 = exchangeRelease.receiver.phoneNumber,
            zipcode = exchangeRelease.receiver.zipCode,
            shippingAddress1 = exchangeRelease.receiver.address,
            shippingMessage = originalConfirmOrder.shippingMessage,
            memo1 = originalConfirmOrder.memo?.memo1,
            memo2 = originalConfirmOrder.memo?.memo2,
            memo3 = originalConfirmOrder.memo?.memo3,
            memo4 = originalConfirmOrder.memo?.memo4,
            memo5 = originalConfirmOrder.memo?.memo5,
            orderItemList = buildOrderItemList(
                exchangeRelease.exchangeProducts
            )
        )

    }

    private fun buildOrderItemList(exchangeProducts: List<ExchangeProduct>): List<OrderItem> {
        return exchangeProducts.map {
            OrderItem(
                salesProductId = it.basicProduct.salesProductId ?: throw NoSuchElementError("salesProductId가 존재하지 않습니다. [basicProductId: ${it.basicProduct.id}]"),
                quantity = it.requestQuantity
            )
        }
    }

    private fun buildExchangeProducts(
        exchangeReleaseCreateModel: ExchangeReleaseCreateModel,
        exchangeRelease: ExchangeRelease
    ): List<ExchangeProduct> {
        return exchangeReleaseCreateModel.returnProducts.map {
            val basicProduct = basicProductRepository.findByIdOrNull(it.basicProductId)
                ?: throw NoSuchElementError("기본상품이 존재하지 않습니다. [basicProductId: ${it.basicProductId}]")
            ExchangeProduct(
                basicProduct = basicProduct,
                requestQuantity = it.quantity,
                exchangeRelease = exchangeRelease
            )
        }
    }

    private fun buildClaim(claimCreateModel: ClaimCreateModel): Claim {
        val releaseInfo = releaseInfoRepository.findByIdOrNull(claimCreateModel.releaseInfoId)
            ?: throw NoSuchElementError("releaseInfo가 존재하지 않습니다.")

        val receiver = claimCreateModel.receiver?.toEntity() ?: releaseInfo.confirmOrder?.receiver
        ?: throw NoSuchElementError("회수지 정보가 없습니다. [releaseInfoId: ${claim.returnInfo?.id}]")

        val returnInfo = buildReturnInfo(claimCreateModel, receiver)

        val claim = claimCreateModel.run {
            Claim(
                partnerId = partnerId,
                claimedAt = claimedAt,
                customerName = customerName,
                claimReason = claimReason,
                memo = memo,
                releaseInfo = releaseInfo
            )
        }

        claim.addReturnInfo(returnInfo)

        return claim
    }

    private fun buildReturnInfo(claimCreateModel: ClaimCreateModel, receiver: Receiver): ReturnInfo {
        val returnInfo = ReturnInfo(
            releaseItemId = null,
            receiver = receiver
        )

        val returnProducts = buildReturnProducts(claimCreateModel, returnInfo)

        returnInfo.returnProducts.addAll(returnProducts)

        return returnInfo
    }

    private fun buildReturnProducts(claimCreateModel: ClaimCreateModel, returnInfo: ReturnInfo): List<ReturnProduct> {
        return claimCreateModel.returnProducts.map {
            val basicProduct = basicProductRepository.findByIdOrNull(it.basicProductId)
                ?: throw NoSuchElementError("기본상품이 존재하지 않습니다. [basicProductId: ${it.basicProductId}]")
            ReturnProduct(
                requestQuantity = it.quantity,
                basicProduct = basicProduct,
                returnInfo = returnInfo
            )
        }
    }
}