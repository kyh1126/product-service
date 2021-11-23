package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.Warehouse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class WarehouseService(
    private val warehouseRepository: WarehouseRepository,
) {
    fun getWarehouse(id: Long): Warehouse {
        return warehouseRepository.findById(id).get()
    }
}
