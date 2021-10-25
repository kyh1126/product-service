package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.PackageProduct
import org.springframework.data.jpa.repository.JpaRepository

interface PackageProductRepository : JpaRepository<PackageProduct, Long>
