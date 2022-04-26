package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.entity.ReleaseProduct
import com.smartfoodnet.fnproduct.release.model.vo.ReleaseStatus
import com.smartfoodnet.fnproduct.release.model.vo.TrackingNumberStatus
import java.time.LocalDateTime

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NosnosReleaseModel(
    val releaseId: Long? = null,
    val memberId: Int? = null,
    val releaseCode: String? = null,
    val orderId: Long? = null,
    val orderCode: String? = null,
    val companyOrderCode: String? = null,
    val shippingMethodId: Int? = null,
    val requestShippingDt: String? = null,
    val releaseDate: String? = null,
    val releaseStatus: Int? = null,
    val shippingOrderInfoId: Int? = null,
    val deliveryAgencyId: Long? = null,
    @JsonProperty("shipping_code")
    val trackingNumber: String? = null,
    val etc1: String? = null,
    val etc2: String? = null,
    val etc3: String? = null,
    val etc4: String? = null,
    val etc5: String? = null,
    val etc6: String? = null,
    val buyerName: String? = null,
    val receiverName: String? = null,
    val tel1: String? = null,
    val tel2: String? = null,
    val zipcode: String? = null,
    val shippingAddress1: String? = null,
    val shippingAddress2: String? = null,
    val shippingMessage: String? = null,
    val channelId: Int? = null,
) {
    fun toEntity(
        releaseProducts: Set<ReleaseProduct>,
        uploadType: OrderUploadType,
        confirmOrder: ConfirmOrder
    ): ReleaseInfo {
        val releaseInfo = ReleaseInfo(
            partnerId = confirmOrder.partnerId,
            orderId = orderId!!,
            orderCode = orderCode!!,
            confirmOrder = confirmOrder,
            releaseId = releaseId!!,
            releaseCode = releaseCode!!,
            releaseStatus = ReleaseStatus.fromReleaseStatus(releaseStatus!!),
            deliveryAgencyId = deliveryAgencyId,
            trackingNumber = trackingNumber,
        )

        return releaseInfo.apply {
            if (trackingNumber != null) {
                trackingNumberStatus = TrackingNumberStatus.getInitialStatus(uploadType)
                trackingNumberCreatedAt = LocalDateTime.now()
            }
            releaseProducts.forEach { addReleaseProducts(it) }
        }
    }
}
