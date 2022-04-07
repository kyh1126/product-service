package com.smartfoodnet.fnproduct.claim.model

import com.smartfoodnet.fnproduct.claim.entity.ReturnInfo
import com.smartfoodnet.fnproduct.order.model.ReceiverModel
import java.time.LocalDateTime

data class ReturnInfoModel(
    val id: Long? = null,
    val releaseItemId: Long? = null,
    val returnInboundCompletedAt: LocalDateTime? = null,
    val receiver: ReceiverModel,
    var returnProducts: List<ReturnProductModel> = listOf(),
) {
    companion object {
        fun from(returnInfo: ReturnInfo): ReturnInfoModel {
            return returnInfo.run {
                ReturnInfoModel(
                    id = id,
                    releaseItemId = releaseItemId,
                    returnInboundCompletedAt = returnInboundCompletedAt,
                    receiver = ReceiverModel.from(receiver),
                    returnProducts = returnProducts.map{ ReturnProductModel.from(it) }
                )
            }
        }
    }
}