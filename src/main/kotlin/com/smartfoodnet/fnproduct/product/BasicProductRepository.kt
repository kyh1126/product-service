package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import org.springframework.data.jpa.repository.JpaRepository

interface BasicProductRepository : JpaRepository<BasicProduct, Long>
