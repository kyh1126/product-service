package com.smartfoodnet.fnproduct.warehouse

import com.smartfoodnet.fnproduct.warehouse.model.response.OutWarehouseModel
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("out-warehouse")
class OutWarehouseController(private val outWarehouseService : OutWarehouseService) {

    @Operation(summary = "특정화주의 출고처 목록")
    @GetMapping("partners/{partnerId}")
    fun getOutWarehouses(@PathVariable partnerId : Long) : List<OutWarehouseModel>{
        return outWarehouseService.getOutWarehouses(partnerId)
    }
}