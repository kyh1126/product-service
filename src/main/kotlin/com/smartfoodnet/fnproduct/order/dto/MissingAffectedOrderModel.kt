package com.smartfoodnet.fnproduct.order.dto

import com.smartfoodnet.fnproduct.order.entity.CollectedOrder

class MissingAffectedOrderModel(
    val collectedId : Long,
    val store: MissingAffectedOrderStoreInfo,
    val orderNumber: String,
    val quantity : Int,
    val orderPrice : Double
){
    companion object{
        fun from(collectedOrder: CollectedOrder) : MissingAffectedOrderModel{
            return collectedOrder.run {
                MissingAffectedOrderModel(
                    collectedId = id ?: 0,
                    store = MissingAffectedOrderStoreInfo(storeId, storeName, storeIcon),
                    orderNumber = orderNumber,
                    quantity = quantity,
                    orderPrice = price ?: 0.0
                )
            }
        }
    }
}

class MissingAffectedOrderStoreInfo(
    val storeId : Long? = null,
    val storeName : String? = null,
    val storeIcon : String? = null
)