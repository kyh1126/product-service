package com.smartfoodnet.fnproduct.order.entity

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import javax.persistence.*

@Entity
class ConfirmProduct(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "confirm_product_id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @JoinColumn(columnDefinition = "BIGINT UNSIGNED")
    @ManyToOne(fetch = FetchType.LAZY)
    val collectedOrder: CollectedOrder? = null,

    @JoinColumn(columnDefinition = "BIGINT UNSIGNED")
    @ManyToOne(fetch = FetchType.LAZY)
    val basicProduct: BasicProduct? = null,

    val packageName: String? = null,

    val packageCode: String? = null,

    val quantity: Int? = null
)