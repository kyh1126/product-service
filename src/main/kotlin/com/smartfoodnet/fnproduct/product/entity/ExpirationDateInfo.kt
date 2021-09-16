package com.smartfoodnet.fnproduct.product.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "expiration_date_info")
class ExpirationDateInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "basic_product_id")
    var basicProductId: Long,

    @Column(name = "manufacture_date_write_yn")
    var manufactureDateWriteYn: String = "N",

    @Column(name = "expiration_date_write_yn")
    var expirationDateWriteYn: String = "N",

    @Column(name = "expiration_date")
    var expirationDate: Int,

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
)
