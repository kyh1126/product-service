package com.smartfoodnet.fnproduct.order

import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.order.dto.CollectedOrderModel
import com.smartfoodnet.fnproduct.order.dto.ConfirmOrderModel
import com.smartfoodnet.fnproduct.order.model.BasicProductAddModel
import com.smartfoodnet.fnproduct.order.model.CollectedOrderCreateModel
import com.smartfoodnet.fnproduct.order.support.condition.CollectingOrderSearchCondition
import com.smartfoodnet.fnproduct.order.support.condition.ConfirmOrderSearchCondition
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
    val orderService: OrderService,
    val orderConfirmService: OrderConfirmService
) {
    @Operation(summary = "주문 생성")
    @PostMapping
    fun createCollectedOrder(@Valid @RequestBody collectedOrderCreateModels: List<CollectedOrderCreateModel>) {
        orderService.createCollectedOrder(collectedOrderCreateModels)
    }

    @Operation(summary = "주문수집 미매칭 쇼핑몰상품 연결")
    @PostMapping("{collectedOrderId}")
    fun addStoreProductToCollectedOrder(
        @Parameter(description = "주문수집 고유 ID", required = true)
        @PathVariable collectedOrderId: Long,
        @RequestBody basicProductAddModel : BasicProductAddModel
    ){
        orderService.addStoreProduct(collectedOrderId, basicProductAddModel)
    }

    @Operation(summary = "출고지시 생성")
    @PostMapping("/partners/{partnerId}/confirm")
    fun createConfirmOrder(
        @PathVariable partnerId: Long,
        @RequestBody collectedIds: List<Long>
    ) {
        orderConfirmService.createConfirmOrder(partnerId, collectedIds)
    }

    @Operation(summary = "특정 화주(고객사) ID 의 주문수집 조회")
    @GetMapping("partners/{partnerId}")
    fun getCollectedOrders(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @Parameter(description = "검색조건")
        @ModelAttribute condition: CollectingOrderSearchCondition,
    ): List<CollectedOrderModel> {
        return orderService.getCollectedOrder(condition.apply { this.partnerId = partnerId })
    }

    @GetMapping("partners/{partnerId}/confirm")
    fun getConfirmOrders(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @Parameter(description = "검색조건")
        @ModelAttribute condition: ConfirmOrderSearchCondition,
    ): List<ConfirmOrderModel> {
        return orderConfirmService.getConfirmOrder(condition.apply { this.partnerId = partnerId })
    }
}