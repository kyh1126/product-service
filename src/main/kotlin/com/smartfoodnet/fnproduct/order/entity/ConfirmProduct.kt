package com.smartfoodnet.fnproduct.order.entity

import com.smartfoodnet.fnproduct.order.vo.MatchingType
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class ConfirmProduct(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_order_id")
    var collectedOrder: CollectedOrder? = null,

    @Enumerated(EnumType.STRING)
    var type: BasicProductType = BasicProductType.BASIC,

    @Enumerated(EnumType.STRING)
    val matchingType: MatchingType? = MatchingType.AUTO,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(columnDefinition = "BIGINT UNSIGNED")
    val basicProduct: BasicProduct,

    val quantity: Int,

    val quantityPerUnit: Int
){
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "confirm_product_id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long = 0
}