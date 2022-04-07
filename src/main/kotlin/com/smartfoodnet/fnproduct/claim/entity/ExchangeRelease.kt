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
@Table(name = "exchange_release")
class ExchangeRelease(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", columnDefinition = "BIGINT UNSIGNED")
    val claim: Claim,

    @Embedded
    var receiver: Receiver,

    @Column(name = "tracking_number")
    val trackingNumber: String? = null,

    @Column(name = "tracking_number_registered_at")
    val trackingNumberRegisteredAt: LocalDateTime? = null,

    @Column(name = "shipping_completed_at")
    val shippingCompletedAt: LocalDateTime? = null,

    @OneToMany(mappedBy = "exchangeRelease", cascade = [CascadeType.PERSIST])
    val exchangeProducts: MutableList<ExchangeProduct> = mutableListOf()
)