package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.common.model.PageableApi
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.product.model.response.BasicProductDetailModel
import com.smartfoodnet.fnproduct.product.model.response.BasicProductModel
import com.smartfoodnet.fnproduct.product.model.response.CategoryByLevelModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("basic-products")
class BasicProductController(private val basicProductService: BasicProductService) {
    @PageableApi
    @Operation(summary = "특정 화주(고객사) ID 의 기본상품 리스트 조회")
    @GetMapping("partners/{partnerId}")
    fun getBasicProducts(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @Parameter(hidden = true)
        @PageableDefault(size = 50, sort = ["id"], direction = Sort.Direction.DESC)
        page: Pageable,
    ): PageResponse<BasicProductModel> {
        return basicProductService.getBasicProducts(partnerId, page)
    }

    @Operation(summary = "기본상품 상세 조회")
    @GetMapping("{productId}")
    fun getBasicProduct(
        @Parameter(description = "상품 ID", required = true)
        @PathVariable productId: Long,
    ): BasicProductDetailModel {
        return basicProductService.getBasicProduct(productId)
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

    @Operation(summary = "기본상품 추가")
    @PostMapping
    fun createBasicProduct(
        @Parameter(description = "대분류") @RequestParam(required = false) level1CategoryId: Long?,
        @Parameter(description = "소분류") @RequestParam(required = false) level2CategoryId: Long?,
    ): List<CategoryByLevelModel> {
        // TODO: 로직 구현 예정
        return basicProductService.getSubsidiaryMaterialCategories(level1CategoryId, level2CategoryId)
    }

}
