package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.common.Constants
import com.smartfoodnet.common.Constants.NOSNOS_CANCEL_STATUS
import java.time.LocalDateTime

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetOutboundCancelModel(
    val cancelId: Long,
    val memberId: Long,
    val orderId: Long,
    val releaseId: Long?,
    val cancelStatus: Int,
    val cancelReasonNo: Int,
    val cancelReasonContent: String? = null,
    val historyList: List<HistoryModel>? = null
) {
    data class CancelledOutboundModel(
        val orderId: Long,
        val releaseId: Long?,
        val cancelStatus: Int,
        val cancelReason: String? = null,
        val pausedAt: LocalDateTime? = null
    )

    fun toCancelledOutboundModel(): CancelledOutboundModel {
        return CancelledOutboundModel(
            orderId = orderId,
            releaseId = releaseId,
            cancelStatus = cancelStatus,
            cancelReason = cancelReasonContent,
            pausedAt = historyList?.firstOrNull { it.cancelStatus == NOSNOS_CANCEL_STATUS }?.createDate
        )
    }
}

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class HistoryModel(
    val cancelStatus: Int,
    val cancelReasonNo: Int,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val createDate: LocalDateTime,
    val cancelReasonContent: String? = null,
)
