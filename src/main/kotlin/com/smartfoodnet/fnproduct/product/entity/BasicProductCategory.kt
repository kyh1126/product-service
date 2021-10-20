package com.smartfoodnet.fnproduct.product.entity

import com.smartfoodnet.fnproduct.code.entity.Code
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
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

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
