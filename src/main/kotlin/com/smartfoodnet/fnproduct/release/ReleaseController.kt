package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.common.model.response.CommonResponse
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.release.model.request.ReleaseInfoSearchCondition
import com.smartfoodnet.fnproduct.release.model.response.OrderConfirmProductModel
import com.smartfoodnet.fnproduct.release.model.response.OrderProductModel
import com.smartfoodnet.fnproduct.release.model.response.ReleaseInfoModel
import com.smartfoodnet.fnproduct.release.model.vo.DeliveryAgency
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Api(description = "릴리즈 관련 API")
@RestController
@RequestMapping("release-info")
class ReleaseController(private val releaseInfoService: ReleaseInfoService) {
    @Operation(summary = "릴리즈 정보 리스트 조회")
    @GetMapping
    fun getReleaseInfoList(
        @Parameter(description = "검색조건")
        @ModelAttribute condition: ReleaseInfoSearchCondition,
        @PageableDefault(size = 50, sort = ["id"], direction = Sort.Direction.DESC) page: Pageable,
    ): PageResponse<ReleaseInfoModel> {
        return releaseInfoService.getReleaseInfoList(condition, page)
    }

    @Operation(summary = "릴리즈 정보 동기화")
    @PostMapping("sync")
    fun syncReleaseInfo(): CommonResponse<String> {
        releaseInfoService.syncReleaseInfo()
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

    @Operation(summary = "주문번호 상세 조회")
    @GetMapping("order-confirm-products")
    fun getConfirmProducts(
        @Parameter(description = "화주(고객사) ID", required = true) @RequestParam partnerId: Long,
        @Parameter(description = "주문번호", required = true) @RequestParam orderNumber: String,
    ): List<OrderConfirmProductModel> {
        return releaseInfoService.getConfirmProducts(partnerId, orderNumber)
    }

    @Operation(summary = "택배사 정보 동기화")
    @PostMapping("delivery-info/sync")
    fun syncDeliveryInfo(
        @Parameter(description = "택배사 정보", required = true)
        @RequestParam deliveryAgency: DeliveryAgency
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
