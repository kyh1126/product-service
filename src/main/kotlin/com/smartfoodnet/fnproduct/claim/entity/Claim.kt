package com.smartfoodnet.fnproduct.claim.entity

import com.smartfoodnet.fnproduct.claim.model.vo.ClaimReason
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimStatus
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "claim")
class Claim(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @Column(name = "claimed_at")
    var claimedAt: LocalDateTime,

    @Column(name = "original_tracking_number")
    var originalTrackingNumber: String,

    @Column(name = "customer_name")
    var customerName: String,

    @Column(name = "claim_reason")
    var claimReason: ClaimReason,

    @Column(name = "exchange_tracking_number")
    var exchangeTrackingNumber: String? = null,

    @Column(name = "status")
    var status: ClaimStatus? = null,

    @Embedded
    var returnRequestDetail: ReturnRequestDetail? = null,

    @Embedded
    var exchangeProductDetail: ExchangeProductDetail? = null,

    @Column(name = "")
    var returnCustomerName: String? = null,

    @Column(name = "")
    var returnPhoneNumber: String? = null,

    @Column(name = "")
    var returnAddress: String? = null,

    @Column(name = "")
    var returnZipcode: String? = null,

    @Column(name = "")
    var returnTrackingNumber: String? = null,

    @Column(name = "")
    var exchangeShippingCompletedAt: LocalDateTime? = null,

    @Column(name = "")
    var exchangeTrackingNumberRegisteredAt: LocalDateTime? = null,

    @Column(name = "")
    var returnShippingCompletedAt: LocalDateTime? = null,

    @Column(name = "")
    var returnRequestedAt: LocalDateTime? = null
)

@Embeddable
data class ReturnRequestDetail(
    @Column(name = "")
    var returningProductName: String? = null,

    @Column(name = "")
    var returningProductCode: String? = null,

    @Column(name = "")
    var returnRequestQuantity: Int? = null,

    @Column(name = "")
    var inboundQuantity: Int? = null,

    @Column(name = "")
    var discardedQuantity: Int? = null,
)

@Embeddable
data class ExchangeProductDetail(
    @Column(name = "")
    var exchangedProductName: String? = null,

    @Column(name = "")
    var exchangedProductCode: String? = null,

    @Column(name = "")
    var exchangeQuantity: Int? = null,

    @Column(name = "exchange_receiver")
    var exchangeReceiver: String? = null,

    @Column(name = "exchange_receiver")
    var exchangeAddress: String? = null,

    @Column(name = "")
    var exchangeZipcode: String? = null,

    @Column(name = "")
    var exchangePhoneNumber: String? = null
)

