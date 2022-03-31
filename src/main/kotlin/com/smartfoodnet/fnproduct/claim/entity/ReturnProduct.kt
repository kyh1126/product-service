package com.smartfoodnet.fnproduct.claim.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "return_product")
class ReturnProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @Column(name = "basic_product_id")
    var basicProductId: Long,

    @Column(name = "shipping_product_id")
    var shippingProductId: Long,

    @Column(name = "quantity")
    var quantity: Int,

    @Column(name = "release_item_id")
    var releaseItemId: Long? = null
)