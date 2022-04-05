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
    val id: Long? = null,

    @Column(name = "request_quantity")
    val requestQuantity: Int,

    @Column(name = "release_item_id")
    val releaseItemId: Long? = null,

    //TODO: 반품입고완료로 수정
    //추적을 어디까지 할 것인가. 배송관리에서 교환출고의 배송정보를 볼 수 있는가
    //
    @Column(name = "shipping_completed_at")
    val shippingCompletedAt: LocalDateTime? = null,

    @Column(name = "inbound_quantity")
    val inboundQuantity: Int? = null,

    @Column(name = "discarded_quantity")
    val discardedQuantity: Int? = null,

    @Embedded
    val receiver: Receiver,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_id", columnDefinition = "BIGINT UNSIGNED")
    val basicProduct: BasicProduct,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", columnDefinition = "BIGINT UNSIGNED")
    val claim: Claim
)