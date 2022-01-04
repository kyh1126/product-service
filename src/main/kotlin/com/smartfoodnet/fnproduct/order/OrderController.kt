package com.smartfoodnet.fnproduct.order

import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.order.model.OrderDetailCreateModel
import com.smartfoodnet.fnproduct.order.model.OrderDetailModel
import com.smartfoodnet.fnproduct.order.support.OrderSearchCondition
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api(description = "주문 관련 API")
@RestController
@RequestMapping("order")
class OrderController(
    private val orderService: OrderService
) {
    @Operation(summary = "주문 생성")
    @PostMapping
    fun createOrderDetail(@Valid @RequestBody orderDetailCreateModels: List<OrderDetailCreateModel>): List<OrderDetailModel> {
        return orderService.createOrderDetail(orderDetailCreateModels)
    }

    @Operation(summary = "특정 화주(고객사) ID 의 주문 리스트 조회")
    @GetMapping("partners/{partnerId}")
    fun getOrderDetails(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @Parameter(description = "검색조건")
        @ModelAttribute condition: OrderSearchCondition,
        @PageableDefault(size = 50, sort = ["id"], direction = Sort.Direction.DESC) page: Pageable,
    ): PageResponse<OrderDetailModel> {
        condition.apply { this.partnerId = partnerId }
        return orderService.getOrderDetails(condition, page)
    }
}