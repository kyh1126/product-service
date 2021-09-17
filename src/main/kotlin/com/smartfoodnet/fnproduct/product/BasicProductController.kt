package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.model.response.BasicProductModel
import com.smartfoodnet.fnproduct.product.model.response.CategoryByLevelModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("basic-products")
class BasicProductController(private val basicProductService: BasicProductService) {

    @Operation(summary = "특정 화주(고객사) ID 의 기본상품 리스트 조회")
    @GetMapping("partners/{partnerId}")
    fun getBasicProducts(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
    ): List<BasicProductModel> {
        return basicProductService.getBasicProducts(partnerId)
    }

    @Operation(summary = "기본상품 카테고리 조회")
    @GetMapping("categories")
    fun getBasicProductCategories(
        @Parameter(description = "대분류") @RequestParam(required = false) level1CategoryId: Long?,
        @Parameter(description = "중분류") @RequestParam(required = false) level2CategoryId: Long?,
    ): List<CategoryByLevelModel> {
        return basicProductService.getBasicProductCategories(level1CategoryId, level2CategoryId)
    }

    @Operation(summary = "부자재 카테고리 조회")
    @GetMapping("subsidiary-material-categories")
    fun getSubsidiaryMaterialCategories(
        @Parameter(description = "대분류") @RequestParam(required = false) level1CategoryId: Long?,
        @Parameter(description = "소분류") @RequestParam(required = false) level2CategoryId: Long?,
    ): List<CategoryByLevelModel> {
        return basicProductService.getSubsidiaryMaterialCategories(level1CategoryId, level2CategoryId)
    }

}
