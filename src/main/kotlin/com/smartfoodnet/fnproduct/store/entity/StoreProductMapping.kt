package com.smartfoodnet.fnproduct.store.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "store_product_mapping")
@Where(clause = "deleted_at is NULL")
class StoreProductMapping(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_product_id", columnDefinition = "BIGINT UNSIGNED", foreignKey = ForeignKey(name = "fk_store_product_mapping__store_product"))
    @JsonIgnore
    var storeProduct: StoreProduct,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_id", columnDefinition = "BIGINT UNSIGNED", foreignKey = ForeignKey(name = "fk_store_product_mapping__basic_product"))
    var basicProduct: BasicProduct,

    @Column(name = "quantity")
    var quantity: Int = 1,
) : BaseEntity() {
    fun delete() {
        deletedAt = LocalDateTime.now()
    }
}
