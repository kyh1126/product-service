package com.smartfoodnet.fnproduct.product.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.common.entity.BaseEntity
import javax.persistence.*

@Entity
class PackageProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_id", columnDefinition = "BIGINT UNSIGNED")
    @JsonIgnore
    var basicProduct: BasicProduct? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_product_id", columnDefinition = "BIGINT UNSIGNED")
    @JsonIgnore
    var packageProduct: BasicProduct,

    @Column(name = "quantity")
    var quantity: Int,
) : BaseEntity()
