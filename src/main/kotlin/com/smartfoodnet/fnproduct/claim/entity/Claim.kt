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

    @Column(name = "partner_id")
    var partnerId: Long,

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
    var exchangeProductDetail: ExchangeProductDetail? = null,

    @Column(name = "return_customer_name")
    var returnCustomerName: String? = null,

    @Column(name = "return_phone_number")
    var returnPhoneNumber: String? = null,

    @Column(name = "return_address")
    var returnAddress: String? = null,

    @Column(name = "return_zipcode")
    var returnZipcode: String? = null,

    @Column(name = "return_tracking_number")
    var returnTrackingNumber: String? = null,

    @Column(name = "exchange_shipping_completed_at")
    var exchangeShippingCompletedAt: LocalDateTime? = null,

    @Column(name = "exchange_tracking_number_registered_at")
    var exchangeTrackingNumberRegisteredAt: LocalDateTime? = null,

    @Column(name = "return_shipping_completed_at")
    var returnShippingCompletedAt: LocalDateTime? = null,

    @Column(name = "return_requested_at")
    var returnRequestedAt: LocalDateTime? = null,

    @Column(name = "memo")
    var memo: String? = null,

    @OneToMany(mappedBy = "claim", cascade = [CascadeType.PERSIST])
    var returnProducts: MutableList<ReturnProduct> = mutableListOf()
)

@Embeddable
data class ExchangeProductDetail(
    @Column(name = "exchanged_product_name")
    var exchangedProductName: String? = null,

    @Column(name = "exchanged_product_code")
    var exchangedProductCode: String? = null,

    @Column(name = "exchanged_quantity")
    var exchangeQuantity: Int? = null,

    @Column(name = "exchange_receiver")
    var exchangeReceiver: String? = null,

    @Column(name = "exchange_address")
    var exchangeAddress: String? = null,

    @Column(name = "exchange_zipcode")
    var exchangeZipcode: String? = null,

    @Column(name = "exchange_phonenumber")
    var exchangePhoneNumber: String? = null
)

