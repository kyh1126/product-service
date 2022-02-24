package com.smartfoodnet.fnproduct.store

import com.smartfoodnet.fnproduct.store.model.request.StoreProductCreateModel
import com.smartfoodnet.fnproduct.store.model.request.StoreProductMappingCreateModel
import com.smartfoodnet.fnproduct.store.model.request.StoreProductUpdateModel
import com.smartfoodnet.fnproduct.store.model.response.StoreProductModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
    fun createStoreProducts(@Valid @RequestBody storeProductCreateModel: StoreProductCreateModel): List<StoreProductModel> {
        return storeProductService.createStoreProducts(storeProductCreateModel)
    }


    @Operation(summary = "쇼핑몰상품 수정")
    @PatchMapping("")
    fun updateStoreProduct(@Valid @RequestBody storeProductUpdateModel: StoreProductUpdateModel): StoreProductModel {
        return storeProductService.updateStoreProduct(storeProductUpdateModel)
    }
    @Operation(summary = "쇼핑몰상품 기본상품 매핑")
    @PostMapping("/map-basic-product")
    fun mapBasicProduct(@Valid @RequestBody storeProductMappingCreateModels: StoreProductMappingCreateModel): StoreProductModel {
        return storeProductService.mapBasicProducts(storeProductMappingCreateModels)
    }

//    @Operation(summary = "쇼핑몰상품 excel 파일로 생성")
//    @PostMapping("/bulk")
//    fun bulkCreateByExcel(
//        @Parameter(description = "엑셀 양식")file: MultipartFile,
//        @Parameter(description = "화주(고객사) ID", required = true) partnerId: Long): List<StoreProductModel> {
//        return storeProductExcelService.createBulkByExcelFile(file, partnerId)
//    }
}