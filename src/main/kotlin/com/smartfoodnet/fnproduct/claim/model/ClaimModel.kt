package com.smartfoodnet.fnproduct.claim.model

import com.smartfoodnet.fnproduct.claim.entity.Claim
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimReason
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimStatus
import java.time.LocalDateTime

data class ClaimModel(
    var id: Long? = null,
    var partnerId: Long,
    var claimedAt: LocalDateTime,
    var originalTrackingNumber: String,
    var customerName: String,
    var claimReason: ClaimReason,
    var status: ClaimStatus? = null,
    var memo: String? = null,
    var returnProducts: List<ReturnProductModel> = mutableListOf(),
    var exchangeProducts: List<ExchangeProductModel> = mutableListOf()
) {
    companion object {
        fun from(claim: Claim): ClaimModel {
            return claim.run {
                ClaimModel(
                    id = id,
                    partnerId = partnerId,
                    claimedAt = claimedAt,
                    originalTrackingNumber = originalTrackingNumber,
                    customerName = customerName,
                    claimReason = claimReason,
                    status = status,
                    memo = memo,
                    returnProducts = returnProducts.map { ReturnProductModel.from(it) },
                    exchangeProducts = exchangeProducts.map { ExchangeProductModel.from(it) }
                )
            }
        }
    }
}
