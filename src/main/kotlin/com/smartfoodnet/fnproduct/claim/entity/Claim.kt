package com.smartfoodnet.fnproduct.claim.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimReason
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimStatus
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "claim")
class Claim(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @Column(name = "partner_id")
    val partnerId: Long,

    @Column(name = "claimed_at")
    val claimedAt: LocalDateTime,

    @Column(name = "original_tracking_number")
    val originalTrackingNumber: String,

    @Column(name = "customer_name")
    val customerName: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "claim_reason")
    val claimReason: ClaimReason,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    val status: ClaimStatus? = null,

    @Column(name = "memo")
    val memo: String? = null,

    @OneToOne(mappedBy = "claim", cascade = [CascadeType.PERSIST])
    var returnInfo: ReturnInfo? = null,

    @OneToOne(mappedBy = "claim", cascade = [CascadeType.PERSIST])
    var exchangeRelease: ExchangeRelease? = null,

    @OneToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "release_info_id", foreignKey = ForeignKey(name = "FK_release_info__claim"), columnDefinition = "BIGINT UNSIGNED")
    val releaseInfo: ReleaseInfo? = null
): BaseEntity()