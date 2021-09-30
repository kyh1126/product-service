package com.smartfoodnet.fnproduct.warehouse

import com.smartfoodnet.fnproduct.warehouse.model.response.OutWarehouseModel
import org.springframework.stereotype.Service

@Service
class OutWarehouseService(
    private val outWarehouseRepository: OutWarehouseRepository
) {
    fun getOutWarehouses(partnerId : Long) : List<OutWarehouseModel>{
        return outWarehouseRepository.findAll().map { OutWarehouseModel.fromEntity(it) }
    }

    fun savOutWarehouse(model : OutWarehouseModel) {

    }
}