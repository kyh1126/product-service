package com.smartfoodnet.fnproduct.warehouse

import com.smartfoodnet.fnproduct.warehouse.entity.InWarehouse
import org.springframework.data.jpa.repository.JpaRepository

interface InWarehouseRepository : JpaRepository<InWarehouse, Long> {
    fun findByPartnerId(partnerId: Long): List<InWarehouse>
}
