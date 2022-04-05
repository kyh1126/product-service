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
@Table(name = "return_product")
class ReturnProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @Column(name = "request_quantity")
    var requestQuantity: Int,

    @Column(name = "release_item_id")
    var releaseItemId: Long? = null,

    @Column(name = "return_tracking_number")
    var trackingNumber: String? = null,

    @Column(name = "return_shipping_completed_at")
    var shippingCompletedAt: LocalDateTime? = null,

    @Column(name = "inbound_quantity")
    var inboundQuantity: Int? = null,

    @Column(name = "discarded_quantity")
    var discardedQuantity: Int? = null,

    @Embedded
    var receiver: Receiver,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_id", columnDefinition = "BIGINT UNSIGNED")
    var basicProduct: BasicProduct,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", columnDefinition = "BIGINT UNSIGNED")
    var claim: Claim? = null
)