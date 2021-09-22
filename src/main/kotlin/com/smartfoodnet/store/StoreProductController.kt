package com.smartfoodnet.store

import com.smartfoodnet.fnproduct.product.BasicProductService
import com.smartfoodnet.fnproduct.product.model.response.BasicProductModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("basic-product")
class StoreProductController(private val basicProductService: BasicProductService) {

    @Operation(summary = "특정 화주(고객사) ID 의 기본상품 리스트 조회")
    @GetMapping("partner/{partnerId}")
    fun getBasicProducts(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
    ): List<BasicProductModel> {
        return basicProductService.getBasicProducts(partnerId)
    }

}
