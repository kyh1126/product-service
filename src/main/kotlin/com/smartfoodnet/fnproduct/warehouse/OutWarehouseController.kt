package com.smartfoodnet.fnproduct.warehouse

import com.smartfoodnet.fnproduct.warehouse.model.dto.OutWarehouseDto
import com.smartfoodnet.fnproduct.warehouse.model.dto.OutWarehouseUpdateDto
import com.smartfoodnet.fnproduct.warehouse.model.response.OutWarehouseModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("out/warehouse")
class OutWarehouseController(private val outWarehouseService: OutWarehouseService) {

    @Operation(summary = "특정화주의 출고처 목록")
    @GetMapping("partners/{partnerId}")
    fun getOutWarehouses(
        @Parameter(description = "화주(고객사) ID") @PathVariable partnerId: Long
    ): List<OutWarehouseModel> {
        return outWarehouseService.getOutWarehouses(partnerId)
    }

    @Operation(summary = "화주 출고처 등록")
    @PostMapping("partners/{partnerId}")
    fun saveOutWarehouse(
        @Parameter(description = "화주(고객사) ID") @PathVariable partnerId: Long,
        @Parameter(description = "출고처 등록 데이터 모델") @RequestBody outWarehouseDto: OutWarehouseDto
    ) {
        outWarehouseService.saveOutWarehouse(partnerId, outWarehouseDto)
    }

    @Operation(summary = "화주 출고처 수정")
    @PutMapping("/{id}")
    fun updateOutWarehouse(
        @Parameter(description = "출고처 고유 ID") @PathVariable id: Long,
        @Parameter(description = "출고처 업데이트 모델") @RequestBody updateDto: OutWarehouseUpdateDto
    ) {
        outWarehouseService.updateOutWarehouse(id, updateDto)
    }
}
