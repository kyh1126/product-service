package com.smartfoodnet.fnproduct.claim.entity

import com.smartfoodnet.fnproduct.order.entity.Receiver
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "exchange_product")
class ExchangeProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_id", columnDefinition = "BIGINT UNSIGNED")
    val basicProduct: BasicProduct,

    @Column(name = "request_quantity")
    val requestQuantity: Int,

    @Column(name = "tracking_number")
    val trackingNumber: String? = null,

    @Column(name = "tracking_number_registered_at")
    val trackingNumberRegisteredAt: LocalDateTime? = null,

    @Column(name = "shipping_completed_at")
    val shippingCompletedAt: LocalDateTime? = null,

    @Embedded
    val receiver: Receiver,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", columnDefinition = "BIGINT UNSIGNED")
    val claim: Claim
)