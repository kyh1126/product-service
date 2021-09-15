package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.Warehouse
import org.springframework.data.jpa.repository.JpaRepository

interface WarehouseRepository : JpaRepository<Warehouse, Long>
