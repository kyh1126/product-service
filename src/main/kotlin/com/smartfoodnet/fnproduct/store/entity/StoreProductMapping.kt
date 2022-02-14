package com.smartfoodnet.fnproduct.store.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "store_product_mapping")
@Where(clause = "deleted_at is NULL")
class StoreProductMapping(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_product_id", columnDefinition = "BIGINT UNSIGNED")
    @JsonIgnore
    var storeProduct: StoreProduct? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_id", columnDefinition = "BIGINT UNSIGNED")
    var basicProduct: BasicProduct? = null,

    @Column(name = "quantity")
    var quantity: Int = 1,
) : BaseEntity() {
    fun delete() {
        deletedAt = LocalDateTime.now()
    }
}
