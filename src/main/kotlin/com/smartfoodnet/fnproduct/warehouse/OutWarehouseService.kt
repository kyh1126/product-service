package com.smartfoodnet.fnproduct.warehouse

import com.smartfoodnet.common.copyNonNullProperty
import com.smartfoodnet.fnproduct.warehouse.model.dto.OutWarehouseDto
import com.smartfoodnet.fnproduct.warehouse.model.dto.OutWarehouseUpdateDto
import com.smartfoodnet.fnproduct.warehouse.model.response.OutWarehouseModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class OutWarehouseService(
    private val outWarehouseRepository: OutWarehouseRepository
) {
    fun getOutWarehouses(partnerId: Long): List<OutWarehouseModel> {
        return outWarehouseRepository.findByPartnerId(partnerId)
            .map { OutWarehouseModel.fromEntity(it) }
    }

    @Transactional
    fun saveOutWarehouse(partnerId: Long, outWarehouseDto: OutWarehouseDto) {
        outWarehouseRepository.save(outWarehouseDto.toEntity(partnerId))
    }

    @Transactional
    fun updateOutWarehouse(warehouseId: Long, updateDto: OutWarehouseUpdateDto) {
        var outWarehouse = outWarehouseRepository.findById(warehouseId).get();

        copyNonNullProperty(updateDto, outWarehouse)
    }

    @Transactional
    fun deleteOutWarehouse(warehouseId: Long){
        val outWarehouse = outWarehouseRepository.findById(warehouseId).get();
        outWarehouse.deletedAt = LocalDateTime.now();
    }

    fun existsOutWarehouse(partnerId: Long, name : String):Boolean{
        return outWarehouseRepository.existsByPartnerIdAndName(partnerId, name);
    }
}
