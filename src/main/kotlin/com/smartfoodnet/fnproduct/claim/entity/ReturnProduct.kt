package com.smartfoodnet.fnproduct.claim.entity

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import javax.persistence.*

@Entity
@Table(name = "return_product")
class ReturnProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_id", columnDefinition = "BIGINT UNSIGNED")
    var basicProduct: BasicProduct,

    @Column(name = "request_quantity")
    var requestQuantity: Int,

    @Column(name = "release_item_id")
    var releaseItemId: Long? = null,

    @Column(name = "inbound_quantity")
    var inboundQuantity: Int? = null,

    @Column(name = "discarded_quantity")
    var discardedQuantity: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", columnDefinition = "BIGINT UNSIGNED")
    var claim: Claim? = null,
)