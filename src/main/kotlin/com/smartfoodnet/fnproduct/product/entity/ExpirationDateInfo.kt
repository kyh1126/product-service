package com.smartfoodnet.fnproduct.product.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "expiration_date_info")
class ExpirationDateInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_id")
    @JsonIgnore
    var basicProduct: BasicProduct,

    @Column(name = "manufacture_date_write_yn")
    var manufactureDateWriteYn: String = "N",

    @Column(name = "expiration_date_write_yn")
    var expirationDateWriteYn: String = "N",

    @Column(name = "expiration_date")
    var expirationDate: Int,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
