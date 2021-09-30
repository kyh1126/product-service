package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface BasicProductRepository : JpaRepository<BasicProduct, Long> {
    fun findByPartnerId(partnerId: Long, page: Pageable): Page<BasicProduct>
}
