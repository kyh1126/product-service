package com.smartfoodnet.fnproduct.product.entity

import com.smartfoodnet.fnproduct.product.vo.SeasonalOption
import com.smartfoodnet.fnproduct.product.vo.SeasonalOptionConverter
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "subsidiary_material")
class SubsidiaryMaterial(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "basic_product_id")
    var basicProductId: Long,

    @Column(name = "seasonal_option")
    @Convert(converter = SeasonalOptionConverter::class)
    var seasonalOption: SeasonalOption,

    @Column(name = "category_id")
    var categoryId: Long,

    @Column(name = "name")
    var name: String,

    @Column(name = "quantity")
    var quantity: Int,

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
)
