package com.smartfoodnet.apiclient.request

import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.vo.DeliveryAgency
import io.swagger.annotations.ApiModelProperty

class TrackingOptionModel(
    val dataModelList: List<TrackingDataModel>
)

class TrackingDataModel(
    @ApiModelProperty(value = "주문번호")
    val ocode: String? = null,
    @ApiModelProperty(value = "받는사람")
    val gname: String? = null,
    @ApiModelProperty(value = "택배사")
    val sender: String? = null,
    @ApiModelProperty(value = "송장번호")
    val sendno: String? = null
) {
    companion object {
        fun fromModel(model: TrackingNumberRegisterModel): TrackingDataModel {
            return TrackingDataModel(
                ocode = model.orderNumber,
                gname = model.receiverName,
                sender = model.deliveryAgency?.playAutoName,
                sendno = model.trackingNumber
            )
        }
    }
}

class TrackingNumberRegisterModel(
    @ApiModelProperty(value = "화주(고객사) ID")
    val partnerId: Long,
    @ApiModelProperty(value = "쇼핑몰 코드")
    val storeCode: String?,
    @ApiModelProperty(value = "주문번호")
    val orderNumber: String? = null,
    @ApiModelProperty(value = "받는사람")
    val receiverName: String? = null,
    @ApiModelProperty(value = "택배사")
    val deliveryAgency: DeliveryAgency? = null,
    @ApiModelProperty(value = "송장번호")
    val trackingNumber: String? = null
) {
    companion object {
        fun fromEntity(
            releaseInfo: ReleaseInfo,
            deliveryAgencyById: Map<Long, DeliveryAgency?>
        ): List<TrackingNumberRegisterModel> {
            val collectedOrders = getCollectedOrders(releaseInfo)

            return collectedOrders.map {
                TrackingNumberRegisterModel(
                    partnerId = it.partnerId,
                    storeCode = it.storeCode,
                    orderNumber = it.orderNumber.split(' ').first(),
                    receiverName = it.receiver.name,
                    deliveryAgency = deliveryAgencyById[releaseInfo.deliveryAgencyId],
                    trackingNumber = releaseInfo.trackingNumber
                )
            }
        }

        private fun getCollectedOrders(releaseInfo: ReleaseInfo) =
            releaseInfo.confirmOrder?.requestOrderList
                ?.map { it.collectedOrder } ?: emptyList()
    }
}
