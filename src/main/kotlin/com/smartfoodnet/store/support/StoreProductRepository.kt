package com.smartfoodnet.store.support

import com.smartfoodnet.store.entity.StoreProduct
import org.springframework.data.jpa.repository.JpaRepository

interface StoreProductRepository : JpaRepository<StoreProduct, Long> {
    fun findAllByPartnerId(partnerId: Long): List<StoreProduct>
}
