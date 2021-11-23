package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.product.model.request.PackageProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.request.PackageProductMappingSearchCondition
import com.smartfoodnet.fnproduct.product.model.response.PackageProductDetailModel
import com.smartfoodnet.fnproduct.product.model.response.PackageProductMappingDetailModel
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api(description = "모음상품 관련 API")
@RestController
@RequestMapping("package-products")
class PackageProductController(private val packageProductService: PackageProductService) {

    @Operation(summary = "특정 화주(고객사) ID 의 모음상품 리스트 조회")
    @GetMapping("partners/{partnerId}")
    fun getPackageProducts(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @Parameter(description = "검색조건")
        @ModelAttribute condition: PackageProductMappingSearchCondition,
        @PageableDefault(size = 50, sort = ["id"], direction = Sort.Direction.DESC) page: Pageable,
    ): PageResponse<PackageProductMappingDetailModel> {
        condition.apply {
            this.partnerId = partnerId
        }
        return packageProductService.getPackageProducts(condition, page)
    }

    @Operation(summary = "모음상품 상세 조회")
    @GetMapping("{productId}")
    fun getPackageProduct(
        @Parameter(description = "상품 ID", required = true)
        @PathVariable productId: Long,
    ): PackageProductDetailModel {
        return packageProductService.getPackageProduct(productId)
    }

    @Operation(summary = "모음상품 추가")
    @PostMapping
    fun createBasicProduct(@Valid @RequestBody packageProductDetailCreateModel: PackageProductDetailCreateModel): PackageProductDetailModel {
        return packageProductService.createPackageProduct(packageProductDetailCreateModel)
    }

    @Operation(summary = "모음상품 수정")
    @PutMapping("{productId}")
    fun updateBasicProduct(
        @Parameter(description = "상품 ID", required = true)
        @PathVariable productId: Long,
        @Valid @RequestBody packageProductDetailCreateModel: PackageProductDetailCreateModel,
    ): PackageProductDetailModel {
        return packageProductService.updatePackageProduct(
            productId,
            packageProductDetailCreateModel
        )
    }
}
