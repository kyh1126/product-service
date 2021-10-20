package com.smartfoodnet.fnproduct.product.entity

import com.smartfoodnet.common.entity.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "partner")
class Partner(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @Column(name = "name")
    var name: String,

    @Column(name = "customer_number")
    var customerNumber: String = String.format("%04d", id),

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,
) : BaseEntity()
