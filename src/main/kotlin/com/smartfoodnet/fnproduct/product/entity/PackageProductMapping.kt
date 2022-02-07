package com.smartfoodnet.fnproduct.product.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.product.model.request.PackageProductMappingCreateModel
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "package_product_mapping")
@Where(clause = "deleted_at is NULL")
class PackageProductMapping(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_product_id", columnDefinition = "BIGINT UNSIGNED")
    @JsonIgnore
    var packageProduct: BasicProduct? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_id", columnDefinition = "BIGINT UNSIGNED")
    var selectedBasicProduct: BasicProduct,

    @Column(name = "quantity")
    var quantity: Int,
) : BaseEntity() {
    fun update(request: PackageProductMappingCreateModel, basicProduct: BasicProduct) {
        selectedBasicProduct = basicProduct
        quantity = request.quantity
    }

    fun delete() {
        deletedAt = LocalDateTime.now()
    }
}
