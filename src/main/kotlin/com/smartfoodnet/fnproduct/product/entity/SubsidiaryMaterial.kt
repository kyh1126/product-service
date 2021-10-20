package com.smartfoodnet.fnproduct.product.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.product.model.request.SubsidiaryMaterialCreateModel
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOption
import com.smartfoodnet.fnproduct.product.model.vo.SeasonalOptionConverter
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "subsidiary_material")
@Where(clause = "deleted_at is NULL")
class SubsidiaryMaterial(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_id", columnDefinition = "BIGINT UNSIGNED")
    @JsonIgnore
    var basicProduct: BasicProduct? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subsidiary_material_id", columnDefinition = "BIGINT UNSIGNED")
    @JsonIgnore
    var subsidiaryMaterial: BasicProduct,

    @Column(name = "seasonal_option")
    @Convert(converter = SeasonalOptionConverter::class)
    var seasonalOption: SeasonalOption,

    @Column(name = "quantity")
    var quantity: Int,
) : BaseEntity() {
    fun update(request: SubsidiaryMaterialCreateModel, basicProductSub: BasicProduct) {
        subsidiaryMaterial = basicProductSub
        seasonalOption = request.seasonalOption
        quantity = request.quantity
    }

    fun delete() {
        deletedAt = LocalDateTime.now()
    }
}
