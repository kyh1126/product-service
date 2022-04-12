package com.smartfoodnet.fnproduct.claim.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ForeignKey
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

    @Column(name = "inbound_quantity")
    val inboundQuantity: Int? = null,

    @Column(name = "discarded_quantity")
    val discardedQuantity: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_id", foreignKey = ForeignKey(name = "FK_basic_product__return_product"), columnDefinition = "BIGINT UNSIGNED")
    val basicProduct: BasicProduct,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_info_id", foreignKey = ForeignKey(name = "FK_return_info__return_product"), columnDefinition = "BIGINT UNSIGNED")
    val returnInfo: ReturnInfo
): BaseEntity()