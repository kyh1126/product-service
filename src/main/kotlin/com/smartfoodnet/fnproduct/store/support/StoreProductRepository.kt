package com.smartfoodnet.fnproduct.store.support

import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import org.springframework.data.jpa.repository.JpaRepository

interface StoreProductRepository : JpaRepository<StoreProduct, Long> {
    fun findAllByPartnerId(partnerId: Long): List<StoreProduct>
    fun findByPartnerIdAndStoreProductCode(partnerId: Long, storeProductCode: String): StoreProduct?
}
