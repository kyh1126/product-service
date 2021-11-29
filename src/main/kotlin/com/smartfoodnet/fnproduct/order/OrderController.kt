package com.smartfoodnet.fnproduct.order

import com.smartfoodnet.fnproduct.order.model.OrderDetailCreateModel
import com.smartfoodnet.fnproduct.order.model.OrderDetailModel
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Api(description = "기본상품 관련 API")
@RestController
@RequestMapping("order")
class OrderController(
    private val orderService: OrderService
) {
    @Operation(summary = "기본상품 추가")
    @PostMapping
    fun createBasicProduct(@Valid @RequestBody orderDetailCreateModels: List<OrderDetailCreateModel>): List<OrderDetailModel> {
        return orderService.createOrderDetail(orderDetailCreateModels)
    }
}