package com.smartfoodnet.fnproduct.order.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.entity.Receiver
import com.smartfoodnet.fnproduct.order.vo.DeliveryType
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import java.time.LocalDateTime

class CollectedOrderSimpleModel(
    val id : Long,
    val orderNumber: String,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val collectedAt : LocalDateTime?,
    val bundleNumber : String,
    val store : StoreSimpleModel?,
    val status: OrderStatus,
    val uploadType: OrderUploadType,
    val price: Double?,
    val receiver : Receiver,
    val deliveryType: DeliveryType,
    val shippingPrice : Double?
){
    companion object{
        fun fromEntity(collectedOrders : List<CollectedOrder>): CollectedOrderSimpleModel{

            val firstData = collectedOrders.first()

            return firstData.run {
                CollectedOrderSimpleModel(
                    id = id!!,
                    orderNumber = orderNumber,
                    collectedAt = collectedAt,
                    bundleNumber = bundleNumber,
                    store = StoreSimpleModel(storeId, storeName, storeIcon),
                    status = status,
                    uploadType = uploadType,
                    price = collectedOrders.sumOf { it.price?:0.0 },
                    receiver = receiver,
                    deliveryType = deliveryType,
                    shippingPrice = shippingPrice
                )
            }
        }
    }
}

class StoreSimpleModel(
    val storeId : Long?,
    val storeName: String?,
    val storeIcon : String?,
)

