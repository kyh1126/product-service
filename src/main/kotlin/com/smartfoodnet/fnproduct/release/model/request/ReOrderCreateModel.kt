package com.smartfoodnet.fnproduct.release.model.request

import com.smartfoodnet.fnproduct.order.vo.DeliveryType

data class ReOrderCreateModel(
    override val receiverName: String,
    override val receiverPhoneNumber: String,
    override val receiverAddress: String,
    override val receiverZipCode: String?,
    override val deliveryType: DeliveryType = DeliveryType.PARCEL,
    override val promotion: String?,
    override val reShipmentReason: String?,
    override var products: List<ManualProductModel>
) : ManualOrderModel()

data class ReOrderProductInfo(
    override var productId: Long,
    override var quantity: Int,
) : ManualProductModel()
