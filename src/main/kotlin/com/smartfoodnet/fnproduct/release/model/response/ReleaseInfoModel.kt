package com.smartfoodnet.fnproduct.release.model.response

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.apiclient.response.NosnosDeliveryAgencyInfoModel
import com.smartfoodnet.fnproduct.claim.model.vo.ExchangeStatus
import com.smartfoodnet.fnproduct.claim.model.vo.ReturnStatus
import com.smartfoodnet.fnproduct.order.vo.DeliveryType
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.dto.SimpleOrderInfoDto
import com.smartfoodnet.fnproduct.release.model.vo.TrackingNumberStatus
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class ReleaseInfoModel(
    @ApiModelProperty(value = "id")
    val id: Long,

    @ApiModelProperty(value = "화주(고객)사 ID", example = "11")
    var partnerId: Long,

    @JsonUnwrapped
    var simpleOrderInfo: SimpleOrderInfoDto,

    @ApiModelProperty(value = "출고상태 (NEW:신규주문/ORDER_CONFIRM:주문접수완료/BEFORE_RELEASE_REQUEST:출고준비중/RELEASE_REQUESTED:출고작업중(1/3)/RELEASE_ORDERED:출고작업중(2/3)/RELEASE_IN_PROGRESS:출고작업중(3/3)/IN_TRANSIT:배송중/COMPLETE:배송완료/RELEASE_PAUSED:출고정지/RELEASE_CANCELLED:출고취소/CANCEL:주문취소)")
    var orderStatus: OrderStatus,

    @ApiModelProperty(value = "NOSNOS 출고 id")
    var releaseId: Long? = null,

    @ApiModelProperty(value = "릴리즈코드")
    var releaseCode: String? = null,

    @ApiModelProperty(value = "택배사명")
    var deliveryAgencyName: String? = null,

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

    @ApiModelProperty(
        value = "배송방식 (PARCEL:택배/VEHICLE:차량/DAWN:새벽배송/SAME_DAY:당일배송)",
        allowableValues = "PARCEL,VEHICLE,DAWN,SAME_DAY"
    )
    var deliveryType: DeliveryType,

    @ApiModelProperty(value = "쇼핑몰 이름")
    var storeName: String,

    @ApiModelProperty(value = "쇼핑몰 아이콘")
    var storeIcon: String? = null,

    @ApiModelProperty(
        value = "수집방식 (API:API/UPLOAD:엑셀/MANUAL:수동)",
        allowableValues = "API,UPLOAD,MANUAL"
    )
    var uploadType: OrderUploadType,

    @ApiModelProperty(value = "묶음번호")
    var bundleNumber: String,
) {
    companion object {
        fun fromEntity(
            releaseInfo: ReleaseInfo,
            deliveryAgencyModelsByDeliveryAgencyId: Map<Long, NosnosDeliveryAgencyInfoModel>
        ): ReleaseInfoModel {
            val collectedOrders = getCollectedOrders(releaseInfo)

            return releaseInfo.run {
                val deliveryAgencyModel = deliveryAgencyModelsByDeliveryAgencyId[deliveryAgencyId]
                val firstCollectedOrder = collectedOrders.first()

                ReleaseInfoModel(
                    id = id!!,
                    partnerId = partnerId,
                    simpleOrderInfo = SimpleOrderInfoDto.from(orderId, orderCode, collectedOrders),
                    orderStatus = releaseStatus.orderStatus,
                    releaseId = releaseId,
                    releaseCode = releaseCode,
                    deliveryAgencyName = deliveryAgencyModel?.deliveryAgencyName,
                    trackingNumber = trackingNumber,
                    trackingNumberStatus = trackingNumberStatus,
                    trackingNumberCreatedAt = trackingNumberCreatedAt,
                    deliveryCompletedAt = deliveryCompletedAt,
                    returnStatus = claim?.returnStatus ?: ReturnStatus.UNREGISTERED,
                    exchangeStatus = claim?.exchangeStatus ?: ExchangeStatus.UNREGISTERED,
                    deliveryType = firstCollectedOrder.deliveryType,
                    storeName = firstCollectedOrder.storeName,
                    storeIcon = firstCollectedOrder.storeIcon,
                    uploadType = firstCollectedOrder.uploadType,
                    bundleNumber = firstCollectedOrder.bundleNumber,
                )
            }
        }

        private fun getCollectedOrders(releaseInfo: ReleaseInfo) =
            releaseInfo.confirmOrder?.requestOrderList
                ?.map { it.collectedOrder } ?: emptyList()
    }
}
