package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.product.model.request.BasicProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.response.BasicProductDetailModel
import com.smartfoodnet.fnproduct.product.model.response.BasicProductModel
import com.smartfoodnet.fnproduct.product.model.response.CategoryByLevelModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("basic-products")
class BasicProductController(private val basicProductService: BasicProductService) {

    @Operation(summary = "특정 화주(고객사) ID 의 기본상품 리스트 조회")
    @GetMapping("partners/{partnerId}")
    fun getBasicProducts(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @Parameter(description = "구분 (BASIC:기본상품/CUSTOM_SUB:고객전용부자재/SUB:공통부자재/PACKAGE:모음상품)")
        @RequestParam(required = false) type: BasicProductType?,
        @PageableDefault(size = 50, sort = ["id"], direction = Sort.Direction.DESC) page: Pageable,
    ): PageResponse<BasicProductModel> {
        return basicProductService.getBasicProducts(partnerId, type, page)
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
    fun createBasicProduct(@Valid @RequestBody basicProductDetailCreateModel: BasicProductDetailCreateModel): BasicProductDetailModel {
        return basicProductService.createBasicProduct(basicProductDetailCreateModel)
    }

    @Operation(summary = "기본상품 수정")
    @PutMapping("{productId}")
    fun updateBasicProduct(
        @Parameter(description = "상품 ID", required = true)
        @PathVariable productId: Long,
        @Valid @RequestBody basicProductDetailCreateModel: BasicProductDetailCreateModel,
    ): BasicProductDetailModel {
        return basicProductService.updateBasicProduct(productId, basicProductDetailCreateModel)
    }

}
