package com.smartfoodnet.fnproduct.claim.entity

import com.smartfoodnet.fnproduct.order.entity.Receiver
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "return_info")
class ReturnInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @Column(name = "release_item_id")
    val releaseItemId: Long? = null,

    //TODO: 반품입고완료로 수정
    @Column(name = "return_inbound_completed_at")
    val returnInboundCompletedAt: LocalDateTime? = null,

    @Embedded
    val receiver: Receiver,

    @OneToMany(mappedBy = "returnInfo", cascade = [CascadeType.PERSIST])
    val returnProducts: MutableList<ReturnProduct> = mutableListOf(),

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", columnDefinition = "BIGINT UNSIGNED")
    val claim: Claim? = null
)