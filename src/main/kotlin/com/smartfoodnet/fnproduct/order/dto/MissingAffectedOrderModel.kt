package com.smartfoodnet.fnproduct.order.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import java.time.LocalDateTime

class MissingAffectedOrderModel(
    val collectedId : Long,
    val store: MissingAffectedOrderStoreInfo,
    val orderNumber: String,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val orderedAt: LocalDateTime?,
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
                    orderedAt = orderedAt,
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