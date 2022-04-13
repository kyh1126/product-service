package com.smartfoodnet.fnproduct.claim.model

import com.smartfoodnet.fnproduct.claim.entity.Claim
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimReason
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimStatus
import java.time.LocalDateTime

data class ClaimModel(
    val id: Long? = null,
    val partnerId: Long,
    val claimedAt: LocalDateTime,
    val originalTrackingNumber: String? = null,
    val customerName: String,
    val claimReason: ClaimReason,
    val status: ClaimStatus? = null,
    val memo: String? = null,
    val returnInfo: ReturnInfoModel,
    var exchangeRelease: ExchangeReleaseModel? = null
) {
    companion object {
        fun from(claim: Claim): ClaimModel {
            return claim.run {
                ClaimModel(
                    id = id,
                    partnerId = partnerId,
                    claimedAt = claimedAt,
                    originalTrackingNumber = releaseInfo?.trackingNumber,
                    customerName = customerName,
                    claimReason = claimReason,
                    status = status,
                    memo = memo,
                    returnInfo = ReturnInfoModel.from(returnInfo!!),
                    exchangeRelease = exchangeRelease?.let { ExchangeReleaseModel.from(it) }
                )
            }
        }
    }
}
