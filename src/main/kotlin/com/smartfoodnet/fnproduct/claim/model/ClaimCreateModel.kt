package com.smartfoodnet.fnproduct.claim.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fnproduct.claim.entity.Claim
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimReason
import com.smartfoodnet.fnproduct.order.model.ReceiverModel
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class ClaimCreateModel(
    @ApiModelProperty(value = "화주(고객사) ID", example = "14")
    val partnerId: Long,
    @ApiModelProperty(value = "반품접수일", example = "2021-12-03 13:22:33")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val claimedAt: LocalDateTime = LocalDateTime.now(),
    @ApiModelProperty(value = "주문자 이름", example = "김미미")
    val customerName: String,
    @ApiModelProperty(value = "반품사유", example = "CHANGED_MIND")
    val claimReason: ClaimReason,
    @ApiModelProperty(value = "배송정보 ID")
    val releaseInfoId: Long,
    val receiver: ReceiverModel? = null,
    @ApiModelProperty(value = "메모")
    val memo: String,
    val returnProducts: List<ReturnProductCreateModel>
) {
    fun toEntity(releaseInfo: ReleaseInfo): Claim {
        return Claim(
            partnerId = partnerId,
            claimedAt = claimedAt,
            customerName = customerName,
            claimReason = claimReason,
            memo = memo,
            releaseInfo = releaseInfo
        )
    }
}

data class ReturnProductCreateModel(
    @ApiModelProperty(value = "기본상품 ID", example = "14")
    val basicProductId: Long,
    @ApiModelProperty(value = "반품요청수량", example = "14")
    val quantity: Int
)
