package com.smartfoodnet.fnproduct.product.entity

import com.smartfoodnet.common.entity.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "shipping_product_archive")
class ShippingProductArchive(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @Column(name = "partner_id", columnDefinition = "BIGINT UNSIGNED")
    var partnerId: Long? = null,

    @Column(name = "shipping_product_id", columnDefinition = "BIGINT UNSIGNED")
    var shippingProductId: Long? = null,

    @Column(name = "product_code")
    var productCode: String? = null,

    @Column(name = "supply_company_id")
    var supplyCompanyId: Int? = null,

    @Column(name = "category_id")
    var categoryId: Int? = null,
) : BaseEntity()
