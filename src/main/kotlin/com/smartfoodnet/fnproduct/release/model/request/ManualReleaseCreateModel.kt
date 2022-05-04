package com.smartfoodnet.fnproduct.release.model.request

import com.smartfoodnet.fnproduct.order.vo.DeliveryType
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType

data class ManualReleaseCreateModel(
    override val receiverName: String,
    override val receiverPhoneNumber: String,
    override val receiverAddress: String,
    override val receiverZipCode: String?,
    override val deliveryType: DeliveryType = DeliveryType.PARCEL,
    override val promotion: String?,
    override val reShipmentReason: String?,
    override var products: List<ManualProductModel>,
    override val uploadType: OrderUploadType = OrderUploadType.MANUAL
) : ManualOrderModel()

data class ManualReleaseProductInfo(
    override var productId: Long,
    override var quantity: Int,
) : ManualProductModel()
