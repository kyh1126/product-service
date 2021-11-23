package com.smartfoodnet.fnproduct.warehouse

import com.smartfoodnet.fnproduct.warehouse.model.dto.InWarehouseDto
import com.smartfoodnet.fnproduct.warehouse.model.dto.InWarehouseUpdateDto
import com.smartfoodnet.fnproduct.warehouse.model.response.InWarehouseModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("in/warehouse")
class InWarehouseController(private val inWarehouseService: InWarehouseService) {

    @Operation(summary = "특정화주의 입고처 목록")
    @GetMapping("partners/{partnerId}")
    fun getInWarehouses(
        @Parameter(description = "화주(고객사) ID") @PathVariable partnerId: Long
    ): List<InWarehouseModel> {
        return inWarehouseService.getInWarehouses(partnerId)
    }

    @Operation(summary = "화주의 입고처 이름 중복 체크")
    @GetMapping("partners/{partnerId}/check")
    fun existsInWarehouse(
        @Parameter(description = "화주(고객사) ID") @PathVariable partnerId: Long,
        @Parameter(description = "중복체크할 이름") @RequestParam name : String
    ):Boolean{
        return inWarehouseService.existsInWarehouse(partnerId, name);
    }


    @Operation(summary = "화주 입고처 등록")
    @PostMapping("partners/{partnerId}")
    fun saveInWarehouse(
        @Parameter(description = "화주(고객사) ID") @PathVariable partnerId: Long,
        @Parameter(description = "입고처 등록 데이터 모델") @RequestBody outWarehouseDto: InWarehouseDto
    ) {
        inWarehouseService.saveInWarehouse(partnerId, outWarehouseDto)
    }

    @Operation(summary = "화주 입고처 수정")
    @PutMapping("/{id}")
    fun updateInWarehouse(
        @Parameter(description = "입고처 고유 ID") @PathVariable id: Long,
        @Parameter(description = "입고처 업데이트 모델") @RequestBody updateDto: InWarehouseUpdateDto
    ) {
        inWarehouseService.updateInWarehouse(id, updateDto)
    }

    @Operation(summary = "화주 입고처 삭제")
    @DeleteMapping("/{id}")
    fun deleteInWarehouse(
        @Parameter(description = "입고처 고유 ID") @PathVariable id: Long
    ) {
        inWarehouseService.deleteInWarehouse(id)
    }
}
