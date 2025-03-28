package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.common.model.response.CommonResponse
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.release.model.request.ReOrderCreateModel
import com.smartfoodnet.fnproduct.release.model.request.ReleaseInfoSearchCondition
import com.smartfoodnet.fnproduct.release.model.response.*
import com.smartfoodnet.fnproduct.release.model.vo.DeliveryAgency
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api(description = "릴리즈 관련 API")
@RestController
@RequestMapping("release-info")
class ReleaseController(
    private val releaseInfoService: ReleaseInfoService,
    private val releaseInfoStoreService: ReleaseInfoStoreService,
) {
    @Operation(summary = "특정 화주(고객사) ID 의 릴리즈 정보 리스트 조회")
    @GetMapping("partners/{partnerId}")
    fun getReleaseInfoList(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @Parameter(description = "검색조건")
        @ModelAttribute condition: ReleaseInfoSearchCondition,
        @PageableDefault(size = 50, sort = ["id"], direction = Sort.Direction.DESC) page: Pageable,
    ): PageResponse<ReleaseInfoModel> {
        condition.apply { this.partnerId = partnerId }

        return releaseInfoService.getReleaseInfoList(condition, page)
    }

    @Operation(summary = "특정 화주(고객사) ID 의 중지된 출고 리스트 조회")
    @GetMapping("partners/{partnerId}/paused")
    fun getPausedReleaseInfoList(
        @Parameter(description = "화주(고객사) ID", required = true)
        @PathVariable partnerId: Long,
        @Parameter(description = "검색조건")
        @ModelAttribute condition: ReleaseInfoSearchCondition,
        @PageableDefault(size = 50, sort = ["id"], direction = Sort.Direction.DESC) page: Pageable,
    ): PageResponse<PausedReleaseInfoModel> {
        condition.apply {
            this.partnerId = partnerId
            setPausedOrderStatus()
        }
        return releaseInfoService.getPausedReleaseInfoList(condition, page)
    }

    @Operation(summary = "출고정보 상세 조회")
    @GetMapping("order/{orderCode}")
    fun getOrderDetail(
        @Parameter(description = "출고번호") @PathVariable orderCode: String,
    ): OrderReleaseInfoModel {
        return releaseInfoService.getOrderDetail(orderCode)
    }

    @Operation(summary = "릴리즈 정보 동기화")
    @PostMapping("sync")
    fun syncReleaseInfo(
        @Parameter(description = "화주(고객사) ID")
        @RequestParam(required = false) partnerId: Long? = null,
    ): CommonResponse<String> {
        releaseInfoService.syncReleaseInfo(partnerId)
        return CommonResponse(HttpStatus.OK.reasonPhrase)
    }

    @Operation(summary = "출고상품 정보 리스트 조회")
    @GetMapping("order-products")
    fun getOrderProducts(
        @Parameter(description = "출고번호") @RequestParam(required = false) orderCode: String? = null,
        @Parameter(description = "릴리즈코드") @RequestParam(required = false) releaseCode: String? = null,
    ): List<OrderProductModel> {
        return when {
            orderCode != null ->
                releaseInfoService.getOrderProductsByOrderCode(orderCode)
            releaseCode != null ->
                releaseInfoService.getOrderProductsByReleaseCode(releaseCode)
            else ->
                throw IllegalArgumentException("출고번호, 릴리즈코드 중 하나는 필수 값입니다.")
        }
    }

    @Operation(summary = "중지된 출고상품 정보 리스트 조회", description = "출고중지, 출고취소 상태의 출고상품만 조회 가능")
    @GetMapping("order-products/paused")
    fun getPausedOrderProducts(
        @Parameter(description = "출고번호") @RequestParam(required = false) orderCode: String? = null,
        @Parameter(description = "릴리즈코드") @RequestParam(required = false) releaseCode: String? = null,
    ): List<OrderProductModel> {
        return when {
            orderCode != null ->
                releaseInfoService.getPausedOrderProductsByOrderCode(orderCode)
            releaseCode != null ->
                releaseInfoService.getPausedOrderProductsByReleaseCode(releaseCode)
            else ->
                throw IllegalArgumentException("출고번호, 릴리즈코드 중 하나는 필수 값입니다.")
        }
    }

    @Operation(summary = "주문번호 상세 조회")
    @GetMapping("order-confirm-products")
    fun getConfirmProducts(
        @Parameter(description = "화주(고객사) ID", required = true) @RequestParam partnerId: Long,
        @Parameter(description = "주문번호", required = true) @RequestParam orderNumber: String,
    ): List<OrderConfirmProductModel> {
        return releaseInfoService.getConfirmProducts(partnerId, orderNumber)
    }

    @Operation(summary = "출고 중지")
    @PatchMapping("pause/{id}")
    fun pauseReleaseInfo(
        @Parameter(description = "릴리즈 정보 ID", required = true) @PathVariable id: Long,
    ): CommonResponse<String> {
        releaseInfoStoreService.pauseReleaseInfo(id)
        return CommonResponse(HttpStatus.OK.reasonPhrase)
    }

    @Operation(summary = "출고 철회")
    @PatchMapping("cancel/{id}")
    fun cancelReleaseInfo(
        @Parameter(description = "릴리즈 정보 ID", required = true) @PathVariable id: Long,
    ): CommonResponse<String> {
        releaseInfoStoreService.cancelReleaseInfo(id)
        return CommonResponse(HttpStatus.OK.reasonPhrase)
    }

    @Operation(summary = "재출고")
    @PostMapping("re-order/{id}")
    fun reOrder(
        @Parameter(description = "릴리즈 정보 ID", required = true) @PathVariable id: Long,
        @Valid @RequestBody reOrderCreateModel: ReOrderCreateModel,
    ): CommonResponse<String> {
        releaseInfoService.reOrder(id, reOrderCreateModel)
        return CommonResponse(HttpStatus.OK.reasonPhrase)
    }

    @Operation(summary = "택배사 정보 동기화")
    @PostMapping("delivery-info/sync")
    fun syncDeliveryInfo(
        @Parameter(description = "택배사 정보")
        @RequestParam(required = false) deliveryAgency: DeliveryAgency? = null
    ): CommonResponse<String> {
        releaseInfoService.syncDeliveryInfo(deliveryAgency)
        return CommonResponse(HttpStatus.OK.reasonPhrase)
    }

    @Operation(summary = "플레이오토 송장 등록")
    @PostMapping("tracking-numbers/play-auto")
    fun registerTrackingNumber(): CommonResponse<String> {
        releaseInfoService.registerTrackingNumber()
        return CommonResponse(HttpStatus.OK.reasonPhrase)
    }
}
