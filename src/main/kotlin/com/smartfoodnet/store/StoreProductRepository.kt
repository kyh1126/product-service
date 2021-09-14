package com.smartfoodnet.store

import org.springframework.data.jpa.repository.JpaRepository

interface StoreProductRepository : JpaRepository<StoreProduct, Long> {

}
