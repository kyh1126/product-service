package com.smartfoodnet.fnproduct.order.dto

import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.entity.Receiver
import com.smartfoodnet.fnproduct.order.vo.DeliveryType
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType

class CollectedOrderSimpleModel(
    val id : Long,
    val orderNumber: String,
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
        fun fromEntity(collectedOrder : CollectedOrder): CollectedOrderSimpleModel{
            return collectedOrder.run {
                CollectedOrderSimpleModel(
                    id = id!!,
                    orderNumber = orderNumber,
                    bundleNumber = bundleNumber,
                    store = StoreSimpleModel(storeId, storeName, storeIcon),
                    status = status,
                    uploadType = uploadType,
                    price = price,
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

