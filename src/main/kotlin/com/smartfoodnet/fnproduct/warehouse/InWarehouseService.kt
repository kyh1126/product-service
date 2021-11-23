package com.smartfoodnet.fnproduct.warehouse

import com.smartfoodnet.common.copyNonNullProperty
import com.smartfoodnet.fnproduct.warehouse.entity.InWarehouse
import com.smartfoodnet.fnproduct.warehouse.model.dto.InWarehouseDto
import com.smartfoodnet.fnproduct.warehouse.model.dto.InWarehouseUpdateDto
import com.smartfoodnet.fnproduct.warehouse.model.response.InWarehouseModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class InWarehouseService(
    private val inWarehouseRepository: InWarehouseRepository
) {
    fun getInWarehouses(partnerId: Long): List<InWarehouseModel> {
        return inWarehouseRepository.findByPartnerId(partnerId)
            .map { InWarehouseModel.fromEntity(it) }
    }

    fun getInWarehouse(warehouseId: Long): InWarehouse {
        return inWarehouseRepository.findById(warehouseId).get()
    }

    @Transactional
    fun saveInWarehouse(partnerId: Long, inWarehouseDto: InWarehouseDto) {
        inWarehouseRepository.save(inWarehouseDto.toEntity(partnerId))
    }

    @Transactional
    fun updateInWarehouse(warehouseId: Long, updateDto: InWarehouseUpdateDto) {
        var inWarehouse = inWarehouseRepository.findById(warehouseId).get();
        copyNonNullProperty(updateDto, inWarehouse)
    }

    @Transactional
    fun deleteInWarehouse(warehouseId: Long){
        val inWarehouse = inWarehouseRepository.findById(warehouseId).get();
        inWarehouse.deletedAt = LocalDateTime.now();
    }

    fun existsInWarehouse(partnerId: Long, name : String):Boolean{
        return inWarehouseRepository.existsByPartnerIdAndName(partnerId, name);
    }
}
