package com.smartfoodnet.fnproduct.order.entity

import com.smartfoodnet.fnproduct.order.vo.MatchingType
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import javax.persistence.*

@Entity
class ConfirmProduct(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "confirm_product_id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_order_id")
    val collectedOrder: CollectedOrder,

    @Enumerated(EnumType.STRING)
    var type: BasicProductType = BasicProductType.BASIC,

    @Enumerated(EnumType.STRING)
    val matchingType: MatchingType = MatchingType.AUTO,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(columnDefinition = "BIGINT UNSIGNED")
    val basicProduct: BasicProduct,

    val quantity: Int,

    val quantityPerUnit: Int
)