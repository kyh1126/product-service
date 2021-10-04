package com.smartfoodnet.store

import com.smartfoodnet.store.model.StoreProductModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.validation.Valid

@RestController
@RequestMapping("store-product")
class StoreProductController(
    private val storeProductService: StoreProductService,
    private val storeProductExcelService: StoreProductExcelService
) {

    @Operation(summary = "특정 화주(고객사) ID 의 쇼핑몰상품 리스트 조회")
    @GetMapping("/partner/{partnerId}")
    fun getStoreProducts(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
    ): List<StoreProductModel> {
        return storeProductService.getStoreProducts(partnerId)
    }

    @Operation(summary = "쇼핑몰상품 생성")
    @PostMapping("")
    fun create(@Valid @RequestBody storeProductModel: StoreProductModel): StoreProductModel {
        return storeProductService.createStoreProduct(storeProductModel);
    }

    @Operation(summary = "쇼핑몰상품 생성")
    @PostMapping("")
    fun bulkCreateByExcel(file: MultipartFile, partnerId: Long): List<StoreProductModel> {
        return storeProductExcelService.createBulkByExcelFile(file, partnerId)
    }
}
