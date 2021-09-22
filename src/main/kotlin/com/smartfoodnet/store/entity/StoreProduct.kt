package com.smartfoodnet.store.entity

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "store_product")
class StoreProduct (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "partner_id")
    var partnerId: Long? = null,

    @Column(name = "name")
    var name: String?,

    @Column(name = "store_product_code")
    var storeProductCode: String?,

    @Column(name = "optional")
    var optional: String? = null,

    @Column(name = "option_code")
    var optionCode: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_id")
    var basicProduct: BasicProduct? = null,

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
) {

}