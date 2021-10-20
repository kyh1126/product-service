package com.smartfoodnet.fnproduct.product.entity

import com.smartfoodnet.common.entity.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "warehouse")
class Warehouse(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @Column(name = "partner_id", columnDefinition = "BIGINT UNSIGNED")
    var partnerId: Long,

    @Column(name = "name")
    var name: String,
) : BaseEntity()
