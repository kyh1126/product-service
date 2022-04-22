package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.common.Constants
import com.smartfoodnet.common.Constants.NOSNOS_DATE_FORMAT
import com.smartfoodnet.common.toLocalDateTime
import com.smartfoodnet.fninventory.inbound.entity.InboundActualDetail
import com.smartfoodnet.fninventory.inbound.entity.InboundExpectedDetail
import com.smartfoodnet.fninventory.inbound.entity.InboundUnplanned
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.exp

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetInboundWorkModel(
    val receivingWorkHistoryId: Long,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val workDate: LocalDateTime,
    val workType: Int,
    val receivingPlanId: Long? = null,
    val receivingType: Int,
    val shippingProductId: Long,
    val quantity: Int,
    val makeDate: String? = null,
    val expireDate: String? = null,
    val locationId: Int? = null,
    val boxQuantity: Int? = null,
    val palletQuantity: Int? = null,
    val workerMemberId: Long? = null,
    val workMemo: String? = null
) {
    fun toEntity(
        inboundExpectedDetail: InboundExpectedDetail,
        basicProduct : BasicProduct
    ): InboundActualDetail {

        val expireMakeDate = convertExpireAndMakeDate(basicProduct)

        return InboundActualDetail(
            inboundExpectedDetail = inboundExpectedDetail,
            receivingWorkHistoryId = receivingWorkHistoryId,
            actualQuantity = if (workType != 9) quantity.toLong() else (-1 * quantity.toLong()),
            boxQuantity = boxQuantity?.toLong(),
            palletQuantity = palletQuantity?.toLong(),
            actualInboundDate = workDate,
            expirationDate = expireMakeDate.expireDate,
            manufactureDate = expireMakeDate.makeDate
        )
    }

    fun toUnplannedEntity(partnerId: Long, basicProduct: BasicProduct?): InboundUnplanned {

        val expireMakeDate = convertExpireAndMakeDate(basicProduct)

        return InboundUnplanned(
            receivingWorkHistoryId = receivingWorkHistoryId,
            partnerId = partnerId,
            shippingProdcutId = shippingProductId,
            basicProduct = basicProduct,
            workType = workType,
            workDate = workDate,
            receivingType = receivingType,
            quantity = quantity,
            expireDate = expireMakeDate.expireDate,
            makeDate = expireMakeDate.makeDate,
            locationId = locationId,
            boxQuantity = boxQuantity,
            palletQuantity = palletQuantity,
            memo = workMemo,
            workerId = workerMemberId
        )
    }

    class ExpireMakeDate(
        val expireDate: LocalDateTime? = null,
        val makeDate: LocalDateTime? = null
    )

    private fun convertExpireAndMakeDate(basicProduct: BasicProduct?): ExpireMakeDate {
        if (basicProduct == null || !basicProduct.expireDateManage()) return ExpireMakeDate()

        if (!expireDate.isNullOrBlank() && !makeDate.isNullOrBlank()) {
            return ExpireMakeDate(
                expireDate.toLocalDateTime(NOSNOS_DATE_FORMAT),
                makeDate.toLocalDateTime(NOSNOS_DATE_FORMAT)
            )
        } else if (!expireDate.isNullOrBlank() && makeDate.isNullOrBlank()) {
            val expireDate : LocalDateTime? = expireDate.toLocalDateTime(NOSNOS_DATE_FORMAT)
            val makeDate: LocalDateTime? =
                if (basicProduct.expireDateManage() && basicProduct.manufactureDateWrite()) {
                    expireDate?.minusDays(basicProduct.manufactureToExpirationDate())
                } else
                    null
            return ExpireMakeDate(expireDate, makeDate)

        } else if (expireDate.isNullOrBlank() && !makeDate.isNullOrBlank()) {
            val makeDate = makeDate.toLocalDateTime(NOSNOS_DATE_FORMAT)
            val expireDate: LocalDateTime? =
                if (basicProduct.expireDateManage() && basicProduct.expirationDateWrite()) {
                        makeDate?.plusDays(basicProduct.manufactureToExpirationDate())
                } else
                    null
            return ExpireMakeDate(expireDate, makeDate)
        }

        return ExpireMakeDate()
    }

    companion object{
        fun testModel(expireDate : String?, makeDate: String?) : GetInboundWorkModel{
            return GetInboundWorkModel(
                receivingWorkHistoryId = 104227,
                workDate = LocalDateTime.now(),
                workType = 1,
                receivingPlanId = 876,
                receivingType = 1,
                shippingProductId = 4488,
                quantity = 10,
                makeDate = makeDate,
                expireDate = expireDate,
                boxQuantity = 5,
                palletQuantity = 1,
                workerMemberId = 10,
                workMemo = "메모"
            )
        }
    }
}
