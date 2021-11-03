package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.PackageProductMapping
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface PackageProductMappingRepository : JpaRepository<PackageProductMapping, Long>,
    PackageProductMappingCustom,
    QuerydslPredicateExecutor<PackageProductMapping> {

    fun findBySelectedBasicProduct_Id(productId: Long): PackageProductMapping?
}
