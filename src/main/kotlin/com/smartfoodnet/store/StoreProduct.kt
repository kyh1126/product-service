package com.smartfoodnet.store

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "store_product")
class StoreProduct (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "name")
    var name: String,

    @Column(name = "store_product_code")
    var storeProductCode: String,

    @Column(name = "option")
    var option: String,

    @Column(name = "option_code")
    var optionCode: String,

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