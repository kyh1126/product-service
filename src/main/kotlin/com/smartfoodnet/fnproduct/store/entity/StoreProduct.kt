package com.smartfoodnet.fnproduct.store.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.store.model.request.StoreProductUpdateModel
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
    name = "store_product",
    uniqueConstraints = [UniqueConstraint(
        name = "store_product_unique_key",
        columnNames = ["store_id", "name", "partner_id", "store_product_code", "option_name", "option_code"]
    )]
)
@Where(clause = "deleted_at is null")
class StoreProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @Column(name = "store_id")
    var storeId: Long,

    @Column(name = "store_name")
    var storeName: String,

    @Column(name = "store_icon")
    var storeIcon: String? = null,

    @Column(name = "partner_id")
    var partnerId: Long,

    @Column(name = "name")
    var name: String,

    @Column(name = "store_product_code")
    var storeProductCode: String?,

    @Column(name = "option_name")
    var optionName: String? = null,

    @Column(name = "option_code")
    var optionCode: String? = null,

    @OneToMany(mappedBy = "storeProduct", cascade = [CascadeType.PERSIST], orphanRemoval = true)
    var storeProductMappings: MutableSet<StoreProductMapping> = mutableSetOf()

) : BaseEntity() {
    fun update(storeProductUpdateModel: StoreProductUpdateModel) {
        storeProductUpdateModel.name?.let { name = it }
        storeProductUpdateModel.optionName?.let { optionName = it }
    }

    fun delete() {
        deletedAt = LocalDateTime.now()
    }

    fun addStoreProductMapping(storeProductMapping: StoreProductMapping) {
        storeProductMappings.add(storeProductMapping)
        storeProductMapping.storeProduct = this
    }

    fun updateStoreProductMappings(newMappings: Set<StoreProductMapping>) {
        storeProductMappings.clear()
        newMappings.forEach { addStoreProductMapping(it) }
    }
}
