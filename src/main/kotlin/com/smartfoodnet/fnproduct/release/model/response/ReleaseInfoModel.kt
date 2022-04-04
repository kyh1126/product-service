package com.smartfoodnet.fnproduct.release.model.response

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.apiclient.response.NosnosDeliveryAgencyInfoModel
import com.smartfoodnet.fnproduct.order.enums.DeliveryType
import com.smartfoodnet.fnproduct.order.model.ReceiverModel
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.vo.ShippingCodeStatus
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class ReleaseInfoModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "NOSNOS 발주 id")
    var orderId: Long? = null,

    @ApiModelProperty(value = "출고번호")
    var orderCode: String? = null,

    @ApiModelProperty(value = "출고상태")
    var orderStatus: String? = null,

    @ApiModelProperty(value = "NOSNOS 출고 id")
    var releaseId: Long? = null,

    @ApiModelProperty(value = "릴리즈코드")
    var releaseCode: String? = null,

    @ApiModelProperty(value = "택배사명")
    var deliveryAgencyName: String? = null,

    @ApiModelProperty(value = "송장번호")
    var shippingCode: String? = null,

    @ApiModelProperty(
        value = "쇼핑몰송장등록 (NONE:송장없음/REGISTERED:자동송장등록성공/REGISTER_FAILED:자동송장등록실패/UNREGISTERED:자동송장등록전/EXCEL_REGISTER:수기등록)",
        allowableValues = "NONE,REGISTERED,REGISTER_FAILED,BEFORE_REGISTER,EXCEL_REGISTER"
    )
    var shippingCodeStatus: ShippingCodeStatus,

    @ApiModelProperty(value = "송장번호부여일시")
    var shippingCodeCreatedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "배송완료일시")
    var deliveryCompletedAt: LocalDateTime? = null,

    @ApiModelProperty(value = "클레임상태")
    var claimStatuses: String? = null,

    @ApiModelProperty(
        value = "배송방식 (PARCEL:택배/VEHICLE:차량/DAWN:새벽배송/SAME_DAY:당일배송)",
        allowableValues = "PARCEL,VEHICLE,DAWN,SAME_DAY"
    )
    var deliveryType: DeliveryType? = null,

    @JsonUnwrapped
    var receiverModel: ReceiverModel? = null,

    @ApiModelProperty(value = "쇼핑몰 이름")
    var storeName: String? = null,

    @ApiModelProperty(
        value = "업로드방식 (API:API/UPLOAD:엑셀/MANUAL:수동)",
        allowableValues = "API,UPLOAD,MANUAL"
    )
    var uploadType: OrderUploadType? = null,

    @ApiModelProperty(value = "묶음번호")
    var bundleNumber: String? = null,

    @ApiModelProperty(value = "주문번호")
    var orderNumbers: String? = null,
) {
    companion object {
        fun fromEntity(
            releaseInfo: ReleaseInfo,
            deliveryAgencyModelsByDeliveryAgencyId: Map<Long, NosnosDeliveryAgencyInfoModel>
        ): ReleaseInfoModel {
            val collectedOrders = getCollectedOrders(releaseInfo)

            return releaseInfo.run {
                val deliveryAgencyModel = deliveryAgencyModelsByDeliveryAgencyId[deliveryAgencyId]
                val firstCollectedOrder = collectedOrders.firstOrNull()

                ReleaseInfoModel(
                    id = id,
                    orderId = orderId,
                    orderCode = orderCode,
                    orderStatus = releaseStatus.orderStatus.description,
                    releaseId = releaseId,
                    releaseCode = releaseCode,
                    deliveryAgencyName = deliveryAgencyModel?.deliveryAgencyName,
                    shippingCode = shippingCode,
                    shippingCodeStatus = shippingCodeStatus,
                    shippingCodeCreatedAt = shippingCodeCreatedAt,
                    deliveryCompletedAt = deliveryCompletedAt,
                    claimStatuses = collectedOrders.joinToString { it.claimStatus ?: "" },
                    deliveryType = firstCollectedOrder?.deliveryType,
                    receiverModel = firstCollectedOrder?.receiver?.run(ReceiverModel::from),
                    storeName = firstCollectedOrder?.storeName,
                    uploadType = firstCollectedOrder?.uploadType,
                    bundleNumber = firstCollectedOrder?.bundleNumber,
                    orderNumbers = collectedOrders.joinToString { it.orderNumber },
                )
            }
        }

        private fun getCollectedOrders(releaseInfo: ReleaseInfo) =
            releaseInfo.confirmOrder?.requestOrderList
                ?.map { it.collectedOrder } ?: emptyList()
    }
}
