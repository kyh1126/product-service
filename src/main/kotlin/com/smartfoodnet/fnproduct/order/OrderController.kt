package com.smartfoodnet.fnproduct.order

import com.smartfoodnet.common.Constants
import com.smartfoodnet.common.model.header.SfnMetaUser
import com.smartfoodnet.fnproduct.order.dto.CollectedOrderModel
import com.smartfoodnet.fnproduct.order.dto.ConfirmProductModel
import com.smartfoodnet.fnproduct.order.model.BasicProductAddModel
import com.smartfoodnet.fnproduct.order.model.CollectedOrderCreateModel
import com.smartfoodnet.fnproduct.order.model.ConfirmProductAddModel
import com.smartfoodnet.fnproduct.order.model.RequestOrderCreateModel
import com.smartfoodnet.fnproduct.order.model.response.ManualOrderResponseModel
import com.smartfoodnet.fnproduct.order.support.condition.CollectingOrderSearchCondition
import com.smartfoodnet.fnproduct.order.support.condition.ConfirmProductSearchCondition
import com.smartfoodnet.fnproduct.release.ManualReleaseService
import com.smartfoodnet.fnproduct.release.model.request.ManualReleaseCreateModel
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore
import javax.validation.Valid

@Api(description = "주문 관련 API")
@RestController
@RequestMapping("order")
class OrderController(
    val orderService: OrderService,
    val confirmOrderService: ConfirmOrderService,
    val manualReleaseService: ManualReleaseService
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
        @RequestBody basicProductAddModel: BasicProductAddModel
    ) {
        orderService.addStoreProduct(basicProductAddModel.apply { this.collectedOrderId = collectedOrderId })
    }

    @Operation(summary = "출고지시 생성")
    @PostMapping("/partners/{partnerId}/confirm")
    fun createConfirmProduct(
        @PathVariable partnerId: Long,
        @RequestBody collectedIds: List<Long>
    ) {
        confirmOrderService.createConfirmProduct(partnerId, collectedIds)
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

    @Operation(summary = "출고지시 조회")
    @GetMapping("partners/{partnerId}/confirm")
    fun getConfirmOrders(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @Parameter(description = "검색조건")
        @ModelAttribute condition: ConfirmProductSearchCondition,
    ): List<ConfirmProductModel> {
        return confirmOrderService.getConfirmProduct(condition.apply { this.partnerId = partnerId })
    }

    @Operation(summary = "발주처리")
    @PostMapping("partners/{partnerId}/reqeust")
    fun createConfirmOrder(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @RequestBody requestOrderCreateModel: RequestOrderCreateModel
    ) {
        confirmOrderService.requestOrders(partnerId, requestOrderCreateModel)
    }

    @Operation(summary = "임시대체상품 추가")
    @PutMapping("partners/{partnerId}/confirm/products")
    fun addBasicProductWithConfirmProduct(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @RequestBody confirmProductAddModel: ConfirmProductAddModel
    ) {
        confirmOrderService.replaceConfirmProducts(confirmProductAddModel.apply { this.partnerId = partnerId })
    }

    @Operation(summary = "기존 기본상품으로 복구")
    @PutMapping("partners/{partnerId}/confirm/products/restore")
    fun restoreConfirmProduct(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @RequestBody collectedIds: List<Long>
    ) {
        confirmOrderService.restoreConfirmProduct(partnerId, collectedIds)
    }

    @Operation(summary = "주문외출고")
    @PostMapping("/partners/{partnerId}/release/manual")
    fun createManualRelease(
        @ApiIgnore
        @RequestHeader(Constants.HEADER_KEY_SFN_META_USER)
        sfnMetaUser: SfnMetaUser?,
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @Parameter(description = "주문외출고 정보", required = true)
        @Valid
        @RequestBody manualReleaseRequest: ManualReleaseCreateModel
    ): ManualOrderResponseModel {
        return manualReleaseService.issueManualRelease(
            sfnMetaUser,
            partnerId,
            manualReleaseRequest
        )
    }
}
