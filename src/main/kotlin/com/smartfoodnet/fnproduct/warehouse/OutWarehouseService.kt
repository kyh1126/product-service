package com.smartfoodnet.fnproduct.warehouse

import com.smartfoodnet.common.copyNonNullProperty
import com.smartfoodnet.fnproduct.warehouse.model.dto.OutWarehouseDto
import com.smartfoodnet.fnproduct.warehouse.model.dto.OutWarehouseUpdateDto
import com.smartfoodnet.fnproduct.warehouse.model.response.OutWarehouseModel
import org.springframework.stereotype.Service

@Service
class OutWarehouseService(
    private val outWarehouseRepository: OutWarehouseRepository
) {
    fun getOutWarehouses(partnerId: Long): List<OutWarehouseModel> {
        return outWarehouseRepository.findByPartnerId(partnerId)
            .map { OutWarehouseModel.fromEntity(it) }
    }

    fun saveOutWarehouse(partnerId: Long, outWarehouseDto: OutWarehouseDto) {
        outWarehouseRepository.save(outWarehouseDto.toEntity(partnerId))
    }

    fun updateOutWarehouse(warehouseId: Long, updateDto: OutWarehouseUpdateDto) {
        var outWarehouse = outWarehouseRepository.findById(warehouseId).get();

        copyNonNullProperty(updateDto, outWarehouse)

        outWarehouseRepository.save(outWarehouse)
    }
}
