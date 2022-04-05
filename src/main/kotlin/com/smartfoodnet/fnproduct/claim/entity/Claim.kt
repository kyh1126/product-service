package com.smartfoodnet.fnproduct.claim.entity

import com.smartfoodnet.fnproduct.claim.model.vo.ClaimReason
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimStatus
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.Table

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

    @Column(name = "status")
    var status: ClaimStatus? = null,

    @Column(name = "memo")
    var memo: String? = null,

    @OneToMany(mappedBy = "claim", cascade = [CascadeType.PERSIST])
    var returnProducts: MutableList<ReturnProduct> = mutableListOf(),

    @OneToMany(mappedBy = "claim", cascade = [CascadeType.PERSIST])
    var exchangeProducts: MutableList<ExchangeProduct> = mutableListOf(),

    @OneToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "release_info_id", columnDefinition = "BIGINT UNSIGNED")
    var releaseInfo: ReleaseInfo
)