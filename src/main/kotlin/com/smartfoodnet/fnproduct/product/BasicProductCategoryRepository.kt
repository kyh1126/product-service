package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory
import org.springframework.data.jpa.repository.JpaRepository

interface BasicProductCategoryRepository : JpaRepository<BasicProductCategory, Long>,
    BasicProductCategoryCustom
