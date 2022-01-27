package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fninventory.inbound.entity.InboundActualDetail
import com.smartfoodnet.fninventory.inbound.entity.InboundExpectedDetail
import com.smartfoodnet.fninventory.inbound.entity.InboundUnplanned
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
        basicProduct: BasicProduct
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
            makeDate = expireMakeDate.makeDate,
            expireDate = expireMakeDate.expireDate,
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
        if (basicProduct == null) return ExpireMakeDate()

        if (!expireDate.isNullOrBlank() && !makeDate.isNullOrBlank()) {
            return ExpireMakeDate(
                LocalDate.parse(expireDate, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(0, 0),
                LocalDate.parse(makeDate, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(0, 0)
            )
        } else if (!expireDate.isNullOrBlank() && makeDate.isNullOrBlank()) {
            val expireDate =
                LocalDate.parse(expireDate, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(0, 0)
            var makeDate: LocalDateTime? = null
            if (basicProduct.expirationDateManagementYn == "Y" && basicProduct.expirationDateInfo?.manufactureDateWriteYn == "Y") {
                makeDate =
                    expireDate.minusDays(basicProduct.expirationDateInfo!!.manufactureToExpirationDate?.toLong()!!)
            }
            return ExpireMakeDate(expireDate, makeDate)
        } else if (expireDate.isNullOrBlank() && !makeDate.isNullOrBlank()) {
            val makeDate =
                LocalDate.parse(makeDate, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(0, 0)
            var expireDate: LocalDateTime? = null
            if (basicProduct.expirationDateManagementYn == "Y" && basicProduct.expirationDateInfo?.expirationDateWriteYn == "Y") {
                expireDate =
                    makeDate.plusDays(basicProduct.expirationDateInfo!!.manufactureToExpirationDate?.toLong()!!)
            }
            return ExpireMakeDate(expireDate, makeDate)
        }

        return ExpireMakeDate()
    }
}
