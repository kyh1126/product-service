package com.smartfoodnet.fnproduct.product.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.code.entity.Code
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "subsidiary_material_category")
class SubsidiaryMaterialCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "level_1_category", nullable = false)
    var level1Category: Code,

    @ManyToOne
    @JoinColumn(name = "level_2_category")
    var level2Category: Code? = null,

    @Column(name = "quantity_apply_yn")
    var quantityApplyYn: String = "N",

    @Column(name = "sort_order")
    var order: Int? = null,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,
) : BaseEntity()
