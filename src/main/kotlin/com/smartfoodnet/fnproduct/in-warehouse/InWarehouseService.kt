package com.smartfoodnet.fnproduct.warehouse

import com.smartfoodnet.common.copyNonNullProperty
import com.smartfoodnet.fnproduct.warehouse.model.dto.InWarehouseDto
import com.smartfoodnet.fnproduct.warehouse.model.dto.InWarehouseUpdateDto
import com.smartfoodnet.fnproduct.warehouse.model.response.InWarehouseModel
import org.springframework.stereotype.Service

@Service
class InWarehouseService(
    private val inWarehouseRepository: InWarehouseRepository
) {
    fun getInWarehouses(partnerId : Long) : List<InWarehouseModel>{
        return inWarehouseRepository.findByPartnerId(partnerId).map { InWarehouseModel.fromEntity(it) }
    }

    fun saveInWarehouse(partnerId: Long, outWarehouseDto: InWarehouseDto)  {
        inWarehouseRepository.save(outWarehouseDto.toEntity(partnerId))
    }

    fun updateInWarehouse(warehouseId: Long, updateDto: InWarehouseUpdateDto) {
        var inWarehouse = inWarehouseRepository.findById(warehouseId).get();

        copyNonNullProperty(updateDto, inWarehouse)

        inWarehouseRepository.save(inWarehouse)
    }
}