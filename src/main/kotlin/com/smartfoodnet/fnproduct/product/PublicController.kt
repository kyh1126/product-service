package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.model.response.CategoryByLevelModel
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Api(tags = ["Public API"])
@RestController
@RequestMapping("public")
class PublicController(private val basicProductController: BasicProductController) {

    @Operation(summary = "기본상품 카테고리 조회", tags = ["Public API", "basic-product-controller"])
    @GetMapping("basic-products/categories")
    fun getBasicProductCategories(
        @Parameter(description = "대분류명") @RequestParam(required = false) level1CategoryName: String?,
        @Parameter(description = "중분류명") @RequestParam(required = false) level2CategoryName: String?,
    ): List<CategoryByLevelModel> {
        return basicProductController.getBasicProductCategories(
            level1CategoryName,
            level2CategoryName
        )
    }
}
