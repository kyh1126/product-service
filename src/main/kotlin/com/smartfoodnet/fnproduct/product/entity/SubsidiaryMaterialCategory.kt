package com.smartfoodnet.fnproduct.product.entity

import com.smartfoodnet.fnproduct.code.entity.Code
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "subsidiary_material_category")
class SubsidiaryMaterialCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_1_category")
    var level1Category: Code,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_2_category")
    var level2Category: Code? = null,

    @Column(name = "quantity_apply_yn")
    var quantityApplyYn: String = "N",

    @Column(name = "sort_order")
    var order: Int? = null,

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
)
