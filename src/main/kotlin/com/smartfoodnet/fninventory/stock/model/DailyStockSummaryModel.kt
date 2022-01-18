package com.smartfoodnet.fninventory.stock.model

import com.smartfoodnet.fninventory.stock.entity.DailyStockSummary
import java.time.LocalDate

data class DailyStockSummaryModel(
    var id: Long? = null,
    var partnerId: Long? = null,
    var basicProductId: Long? = null,
    var shippingProductId: Long,
    var inboundQuantity: Int = 0,
    var outboundQuantity: Int = 0,
    var returnQuantity: Int = 0,
    var outQuantity: Int = 0,
    var returnBackQuantity: Int = 0,
    var returnReceiveQuantity: Int = 0,
    var rollbackReceiveQuantity: Int = 0,
    var adjustInQuantity: Int = 0,
    var adjustOutQuantity: Int = 0,
    var totalStockCount: Int = 0,
    var availableStockCount: Int = 0,
    var totalStockChangeCount: Int = 0,
    var effectiveDate: LocalDate
) {
    companion object {
        fun from(dailyStockSummary: DailyStockSummary): DailyStockSummaryModel {
            return dailyStockSummary.run {
                DailyStockSummaryModel(
                    id = id,
                    partnerId = partnerId,
                    basicProductId = basicProduct.id,
                    shippingProductId = shippingProductId,
                    inboundQuantity = inboundQuantity,
                    outboundQuantity = outboundQuantity,
                    returnQuantity = returnQuantity,
                    outQuantity = outQuantity,
                    returnBackQuantity = returnBackQuantity,
                    returnReceiveQuantity = returnReceiveQuantity,
                    rollbackReceiveQuantity = rollbackReceiveQuantity,
                    adjustInQuantity = adjustInQuantity,
                    adjustOutQuantity = adjustOutQuantity,
                    totalStockCount = totalStockCount,
                    availableStockCount = availableStockCount,
                    totalStockChangeCount = totalStockChangeCount,
                    effectiveDate = effectiveDate
                )
            }
        }
    }
}
