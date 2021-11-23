package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.product.model.request.BasicProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.request.BasicProductSearchCondition
import com.smartfoodnet.fnproduct.product.model.response.BasicProductDetailModel
import com.smartfoodnet.fnproduct.product.model.response.BasicProductModel
import com.smartfoodnet.fnproduct.product.model.response.CategoryByLevelModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api(description = "기본상품 관련 API")
@RestController
@RequestMapping("basic-products")
class BasicProductController(private val basicProductService: BasicProductService) {

    @Operation(summary = "특정 화주(고객사) ID 의 기본상품 리스트 조회")
    @GetMapping("partners/{partnerId}")
    fun getBasicProducts(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @Parameter(description = "검색조건")
        @ModelAttribute condition: BasicProductSearchCondition,
        @PageableDefault(size = 50, sort = ["id"], direction = Sort.Direction.DESC) page: Pageable,
    ): PageResponse<BasicProductModel> {
        condition.apply { this.partnerId = partnerId }
        return basicProductService.getBasicProducts(condition, page)
    }

    @Operation(summary = "공통 부자재 리스트 조회")
    @GetMapping("sub")
    fun getBasicProductsSub(
        @PageableDefault(size = 50, sort = ["id"], direction = Sort.Direction.DESC) page: Pageable,
    ): PageResponse<BasicProductModel> {
        val searchCondition =
            BasicProductSearchCondition().apply { types = setOf(BasicProductType.SUB) }

        return basicProductService.getBasicProducts(condition = searchCondition, page = page)
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
        return basicProductService.getSubsidiaryMaterialCategories(
            level1CategoryId,
            level2CategoryId
        )
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
