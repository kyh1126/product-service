package com.smartfoodnet.fnproduct.warehouse

import com.smartfoodnet.fnproduct.warehouse.entity.OutWarehouse
import org.springframework.data.jpa.repository.JpaRepository

interface OutWarehouseRepository : JpaRepository<OutWarehouse, Long>{
    fun findByPartnerId(partnerId : Long) : List<OutWarehouse>
}