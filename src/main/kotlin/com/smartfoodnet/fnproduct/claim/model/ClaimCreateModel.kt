package com.smartfoodnet.fnproduct.claim.model

import com.smartfoodnet.fnproduct.claim.entity.Claim
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimReason
import com.smartfoodnet.fnproduct.order.model.ReceiverModel
import java.time.LocalDateTime

data class ClaimCreateModel(
    val partnerId: Long,
    val claimedAt: LocalDateTime,
    val originalTrackingNumber: String,
    val customerName: String,
    val claimReason: ClaimReason,
    val releaseInfoId: Long,
    val receiver: ReceiverModel,
    val returnRequestedAt: LocalDateTime? = null,
    val memo: String,
    val returnProducts: List<ReturnProductCreateModel>
) {
    fun toEntity(): Claim {
        return Claim(
            partnerId = partnerId,
            claimedAt = claimedAt,
            originalTrackingNumber = originalTrackingNumber,
            customerName = customerName,
            claimReason = claimReason,
            receiver = receiver.toEntity(),
            memo = memo
        )
    }
}


data class ReturnProductCreateModel(
    val shippingProductId: Long,
    val basicProductId: Long,
    val quantity: Int
)
