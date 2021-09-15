package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.Supplier
import org.springframework.data.jpa.repository.JpaRepository

interface SupplierRepository : JpaRepository<Supplier, Long>
