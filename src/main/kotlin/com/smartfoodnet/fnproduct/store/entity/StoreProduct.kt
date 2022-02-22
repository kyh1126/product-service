package com.smartfoodnet.fnproduct.store.entity

import com.smartfoodnet.common.entity.BaseEntity
import org.hibernate.annotations.Where
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "store_product")
@Where(clause = "deleted_at is null")
class StoreProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "store_code")
    var storeCode: String,

    @Column(name = "store_name")
    var storeName: String,

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

    @OneToMany(mappedBy = "storeProduct", cascade = [CascadeType.PERSIST])
    var storeProductMappings: MutableSet<StoreProductMapping> = mutableSetOf()

) : BaseEntity()
