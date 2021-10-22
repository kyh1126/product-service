package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface BasicProductRepository : JpaRepository<BasicProduct, Long>, BasicProductCustom,
    QuerydslPredicateExecutor<BasicProduct> {
    fun findByCode(code: String): BasicProduct?
    fun findByPartnerIdAndBarcode(partnerId: Long, barcode: String): BasicProduct?
    fun findByPartnerIdAndName(partnerId: Long, name: String): BasicProduct?
}
