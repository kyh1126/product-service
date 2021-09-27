package com.smartfoodnet.fnproduct.product.entity

import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOption
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOptionConverter
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
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

    @Column(name = "subsidiary_material_id")
    var subsidiaryMaterialId: Long?,

    @Column(name = "seasonal_option")
    @Convert(converter = SeasonalOptionConverter::class)
    var seasonalOption: SeasonalOption,

    @Column(name = "name")
    var name: String,

    @Column(name = "quantity")
    var quantity: Int,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
