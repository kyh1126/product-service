package com.smartfoodnet.fnproduct.release.model.response

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.order.model.ReceiverModel
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.vo.PausedBy
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class PausedReleaseInfoModel(
    @ApiModelProperty(value = "id")
    val id: Long,

    @ApiModelProperty(value = "화주(고객)사 ID", example = "11")
    var partnerId: Long,

    @ApiModelProperty(value = "NOSNOS 발주 id")
    var orderId: Long,

    @ApiModelProperty(value = "출고번호")
    var orderCode: String,

    @ApiModelProperty(value = "NOSNOS 출고 id")
    var releaseId: Long? = null,

    @ApiModelProperty(value = "릴리즈코드")
    var releaseCode: String? = null,

    @JsonUnwrapped
    var receiverModel: ReceiverModel,

    @ApiModelProperty(value = "주문번호")
    var orderNumbers: List<String> = emptyList(),

    @ApiModelProperty(value = "주문일시")
    var orderedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "주문수집일시")
    var collectedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "송장번호")
    var trackingNumber: String? = null,

    @ApiModelProperty(value = "송장번호부여일시")
    var trackingNumberCreatedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "출고중지일시")
    var pausedAt: LocalDateTime,

    @ApiModelProperty(value = "출고중지사유")
    var pausedReason: String? = null,

    @ApiModelProperty(value = "출고중지당사자")
    var pausedBy: PausedBy? = null
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
                    orderId = orderId,
                    orderCode = orderCode,
                    releaseId = releaseId,
                    releaseCode = releaseCode,
                    receiverModel = firstCollectedOrder.receiver.run(ReceiverModel::from),
                    orderNumbers = collectedOrders.map { it.orderNumber },
                    orderedAt = firstCollectedOrder.orderedAt,
                    collectedAt = firstCollectedOrder.collectedAt,
                    trackingNumber = trackingNumber,
                    trackingNumberCreatedAt = trackingNumberCreatedAt,
                    pausedAt = pausedAt!!,
                    pausedReason = pausedReason,
                    pausedBy = pausedBy
                )
            }
        }

        private fun getCollectedOrders(releaseInfo: ReleaseInfo) =
            releaseInfo.confirmOrder?.requestOrderList
                ?.map { it.collectedOrder } ?: emptyList()
    }
}
