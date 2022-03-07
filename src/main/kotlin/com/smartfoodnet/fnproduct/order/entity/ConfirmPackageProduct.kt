package com.smartfoodnet.fnproduct.order.entity

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import javax.persistence.*

@Entity
class ConfirmPackageProduct(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="confirm_package_product_id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirm_product_id", columnDefinition = "BIGINT UNSIGNED")
    var confirmProduct : ConfirmProduct,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basicProduct_id", columnDefinition = "BIGINT UNSIGNED")
    val basicProduct : BasicProduct,

    val quantity : Int,
)