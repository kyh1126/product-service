package com.smartfoodnet.fnproduct.claim.model

import com.smartfoodnet.fnproduct.claim.model.vo.ClaimReason
import java.time.LocalDateTime

data class ClaimCreateModel(
    val claimedAt: LocalDateTime,
    val originalTrackingNumber: String,
    val customerName: String,
    val claimReason: ClaimReason,
    val returnCustomerName: String? = null,
    val returnPhoneNumber: String? = null,
    val returnAddress: String? = null,
    val returnZipcode: String? = null,
    val returnRequestedAt: LocalDateTime? = null,
)

data class returnProduct(
    val returnProductId: Long,
    val quantity: Int
)
