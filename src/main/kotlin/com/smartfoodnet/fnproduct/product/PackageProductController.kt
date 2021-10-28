package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.product.model.request.PackageProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.request.PackageProductMappingSearchCondition
import com.smartfoodnet.fnproduct.product.model.response.PackageProductDetailModel
import com.smartfoodnet.fnproduct.product.model.response.PackageProductMappingModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

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
    ): PageResponse<PackageProductMappingModel> {
        condition.apply {
            this.partnerId = partnerId
        }
        return packageProductService.getPackageProducts(condition, page)
    }

    // TODO: 모음상품등록 Flow 시안 리뷰에 따른 설계 변경중...
    @Operation(summary = "모음상품 추가")
    @PostMapping
    fun createBasicProduct(@Valid @RequestBody packageProductDetailCreateModel: PackageProductDetailCreateModel): PackageProductDetailModel {
        return packageProductService.createPackageProduct(packageProductDetailCreateModel)
    }
}
