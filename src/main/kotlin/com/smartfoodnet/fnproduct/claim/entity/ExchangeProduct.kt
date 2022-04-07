package com.smartfoodnet.fnproduct.claim.entity

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import javax.persistence.Column
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_release_id", columnDefinition = "BIGINT UNSIGNED")
    val exchangeRelease: ExchangeRelease
)