package com.smartfoodnet.fnproduct.release.model.response

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.claim.model.vo.ExchangeStatus
import com.smartfoodnet.fnproduct.claim.model.vo.ReturnStatus
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.dto.SimpleOrderInfoDto
import com.smartfoodnet.fnproduct.release.model.vo.PausedBy
import com.smartfoodnet.fnproduct.release.model.vo.TrackingNumberStatus
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class PausedReleaseInfoModel(
    @ApiModelProperty(value = "id")
    val id: Long,

    @ApiModelProperty(value = "화주(고객)사 ID", example = "11")
    var partnerId: Long,

    @JsonUnwrapped
    var simpleOrderInfo: SimpleOrderInfoDto,

    @ApiModelProperty(value = "NOSNOS 출고 id")
    var releaseId: Long? = null,

    @ApiModelProperty(value = "릴리즈코드")
    var releaseCode: String? = null,

    @ApiModelProperty(value = "주문일시")
    var orderedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "주문수집일시")
    var collectedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "송장번호")
    var trackingNumber: String? = null,

    @ApiModelProperty(
        value = "쇼핑몰송장등록 (NONE:송장없음/REGISTERED:자동등록성공/REGISTER_FAILED:자동등록실패/BEFORE_REGISTER:자동등록전/WAITING_CALLBACK:자동등록전(콜백대기중)/EXCEL_REGISTER:수기등록)",
        allowableValues = "NONE,REGISTERED,REGISTER_FAILED,BEFORE_REGISTER,WAITING_CALLBACK,EXCEL_REGISTER"
    )
    var trackingNumberStatus: TrackingNumberStatus,

    @ApiModelProperty(value = "송장번호부여일시")
    var trackingNumberCreatedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "배송완료일시")
    var deliveryCompletedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "반품상태 (UNREGISTERED:미등록/RETURN_REQUESTED:반품요청/RETURN_IN_PROGRESS:반품진행/RETURN_INBOUND_COMPLETED:반품입고완료/RETURN_CANCELLED:반품취소)")
    var returnStatus: ReturnStatus,

    @ApiModelProperty(value = "교환출고상태 (UNREGISTERED:미등록/EXCHANGE_RELEASE_IN_PROGRESS:교환출고중/EXCHANGE_DELIVERY_COMPLETED:교환배송완료)")
    var exchangeStatus: ExchangeStatus,

    @ApiModelProperty(value = "출고중지일시")
    var pausedAt: LocalDateTime,

    @ApiModelProperty(value = "출고중지사유")
    var pausedReason: String? = null,

    @ApiModelProperty(value = "출고중지당사자")
    var pausedBy: PausedBy? = null,

    @ApiModelProperty(value = "쇼핑몰 이름")
    var storeName: String,

    @ApiModelProperty(value = "쇼핑몰 아이콘")
    var storeIcon: String? = null,

    @ApiModelProperty(value = "묶음번호")
    var bundleNumber: String,
) {
    companion object {
        fun fromEntity(
            releaseInfo: ReleaseInfo,
        ): PausedReleaseInfoModel {
            val collectedOrders = getCollectedOrders(releaseInfo)
            val firstCollectedOrder = collectedOrders.first()

            return releaseInfo.run {
                PausedReleaseInfoModel(
                    id = id!!,
                    partnerId = partnerId,
                    simpleOrderInfo = SimpleOrderInfoDto.from(orderId, orderCode, collectedOrders),
                    releaseId = releaseId,
                    releaseCode = releaseCode,
                    orderedAt = firstCollectedOrder.orderedAt,
                    collectedAt = firstCollectedOrder.collectedAt,
                    trackingNumber = trackingNumber,
                    trackingNumberStatus = trackingNumberStatus,
                    trackingNumberCreatedAt = trackingNumberCreatedAt,
                    deliveryCompletedAt = deliveryCompletedAt,
                    returnStatus = claim?.returnStatus ?: ReturnStatus.UNREGISTERED,
                    exchangeStatus = claim?.exchangeStatus ?: ExchangeStatus.UNREGISTERED,
                    pausedAt = pausedAt!!,
                    pausedReason = pausedReason,
                    pausedBy = pausedBy,
                    storeName = firstCollectedOrder.storeName,
                    storeIcon = firstCollectedOrder.storeIcon,
                    bundleNumber = firstCollectedOrder.bundleNumber
                )
            }
        }

        private fun getCollectedOrders(releaseInfo: ReleaseInfo) =
            releaseInfo.confirmOrder?.requestOrderList
                ?.map { it.collectedOrder } ?: emptyList()
    }
}
