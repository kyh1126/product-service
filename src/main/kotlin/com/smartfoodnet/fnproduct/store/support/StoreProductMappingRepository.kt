package com.smartfoodnet.fnproduct.store.support

import com.smartfoodnet.fnproduct.store.entity.StoreProductMapping
import org.springframework.data.jpa.repository.JpaRepository

interface StoreProductMappingRepository : JpaRepository<StoreProductMapping, Long> {
}
