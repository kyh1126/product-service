package com.smartfoodnet.fnproduct.claim.model

import com.smartfoodnet.fnproduct.claim.entity.Claim
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimReason
import java.time.LocalDateTime

data class ClaimCreateModel(
    val claimedAt: LocalDateTime,
    val originalTrackingNumber: String,
    val customerName: String,
    val claimReason: ClaimReason,
    val releaseInfoId: Long,
    val returnCustomerName: String? = null,
    val returnPhoneNumber: String? = null,
    val returnAddress: String? = null,
    val returnZipcode: String? = null,
    val returnRequestedAt: LocalDateTime? = null,
    val returnProducts: List<ReturnProductCreateModel>
) {
    fun toEntity(): Claim {
        return Claim(
            claimedAt = claimedAt,
            originalTrackingNumber = originalTrackingNumber,
            customerName = customerName,
            claimReason = claimReason,
            returnCustomerName = returnCustomerName,
            returnPhoneNumber = returnPhoneNumber,
            returnAddress = returnAddress,
            returnZipcode = returnZipcode,
            returnRequestedAt = returnRequestedAt
        )
    }
}


data class ReturnProductCreateModel(
    val shippingProductId: Long,
    val basicProductId: Long,
    val quantity: Int
)
