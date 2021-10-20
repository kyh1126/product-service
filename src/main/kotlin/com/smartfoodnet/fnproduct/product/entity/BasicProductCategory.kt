package com.smartfoodnet.fnproduct.product.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.code.entity.Code
import javax.persistence.*

@Entity
@Table(name = "basic_product_category")
class BasicProductCategory(
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

    @Column(name = "sort_order")
    var order: Int? = null,
) : BaseEntity()
