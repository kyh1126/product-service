package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.product.model.request.PackageProductSearchCondition
import com.smartfoodnet.fnproduct.product.model.response.PackageProductDetailModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("package-products")
class PackageProductController(private val packageProductService: PackageProductService) {

    @Operation(summary = "특정 화주(고객사) ID 의 모음상품 리스트 조회")
    @GetMapping("partners/{partnerId}")
    fun getPackageProducts(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @Parameter(description = "검색조건")
        @ModelAttribute condition: PackageProductSearchCondition,
        @PageableDefault(size = 50, sort = ["id"], direction = Sort.Direction.DESC) page: Pageable,
    ): PageResponse<PackageProductDetailModel> {
        condition.apply {
            this.partnerId = partnerId
            this.type = BasicProductType.PACKAGE
        }
        return packageProductService.getPackageProducts(condition, page)
    }

}
