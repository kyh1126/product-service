package com.smartfoodnet.fnproduct.claim.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimReason
import com.smartfoodnet.fnproduct.claim.model.vo.ExchangeStatus
import com.smartfoodnet.fnproduct.claim.model.vo.ReturnStatus
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import java.time.LocalDateTime
import javax.persistence.*

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

    @Column(name = "customer_name")
    val customerName: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "claim_reason")
    val claimReason: ClaimReason,

    @Enumerated(EnumType.STRING)
    @Column(name = "return_status")
    var returnStatus: ReturnStatus = ReturnStatus.RETURN_REQUESTED,

    @Enumerated(EnumType.STRING)
    @Column(name = "exchange_status")
    val exchangeStatus: ExchangeStatus = ExchangeStatus.UNREGISTERED,

    @Column(name = "memo")
    val memo: String? = null,

    @OneToOne(mappedBy = "claim", cascade = [CascadeType.PERSIST], fetch = FetchType.EAGER)
    var returnInfo: ReturnInfo? = null,

    @OneToOne(mappedBy = "claim", cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    var exchangeRelease: ExchangeRelease? = null,

    @OneToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    @JoinColumn(name = "release_info_id", foreignKey = ForeignKey(name = "FK_release_info__claim"), columnDefinition = "BIGINT UNSIGNED")
    var releaseInfo: ReleaseInfo? = null
) : BaseEntity() {
    fun addReturnInfo(returnInfo: ReturnInfo) {
        this.returnInfo = returnInfo
        returnInfo.claim = this
    }

    fun addReleaseInfo(releaseInfo: ReleaseInfo) {
        this.releaseInfo = releaseInfo
        releaseInfo.claim = this
    }
}
