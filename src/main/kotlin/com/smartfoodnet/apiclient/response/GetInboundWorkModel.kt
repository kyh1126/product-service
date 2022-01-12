package com.smartfoodnet.apiclient.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fninventory.inbound.entity.InboundActualDetail
import com.smartfoodnet.fninventory.inbound.entity.InboundExpectedDetail
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.exp

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetInboundWorkModel(
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val work_date : LocalDateTime,
    val work_type : Int,
    val receiving_plan_id : Long? = null,
    val receiving_type : Int,
    val shipping_product_id : Long,
    val quantity : Int,
    val make_date : String? = null,
    val expire_date : String? = null,
    val location_id : Int? = null,
    val box_quantity : Int? = null,
    val pallet_quantity : Int? = null,
    val worker_member_id : Long? = null,
    val work_memo : String? = null
){
    fun toEntity(inboundExpectedDetail: InboundExpectedDetail, basicProduct : BasicProduct) : InboundActualDetail {

        val expireMakeDate = convertExpireAndMakeDate(basicProduct)

        return InboundActualDetail(
            inboundExpectedDetail = inboundExpectedDetail,
            actualQuantity = quantity.toLong(),
            boxQuantity = box_quantity?.toLong(),
            palletQuantity = pallet_quantity?.toLong(),
            actualInboundDate = work_date,
            expirationDate = expireMakeDate.expireDate,
            manufactureDate = expireMakeDate.makeDate
        )
    }

    class ExpireMakeDate(
        val expireDate : LocalDateTime? = null,
        val makeDate : LocalDateTime? = null
    )

    private fun convertExpireAndMakeDate(basicProduct: BasicProduct) : ExpireMakeDate {
        if (expire_date.isNullOrBlank() && make_date != null){
            return ExpireMakeDate(
                LocalDate.parse(expire_date, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(0, 0),
                LocalDate.parse(make_date, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(0, 0)
            )
        }
        else if(expire_date != null && make_date == null){
            val expireDate = LocalDate.parse(expire_date, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(0, 0)
            var makeDate : LocalDateTime? = null
            if (basicProduct.expirationDateManagementYn == "Y" && basicProduct.expirationDateInfo?.manufactureDateWriteYn == "Y"){
                makeDate = expireDate.minusDays(basicProduct.expirationDateInfo!!.expirationDate?.toLong()!!)
            }
            return ExpireMakeDate(expireDate, makeDate)
        }
        else if(expire_date == null && make_date != null){
            val makeDate = LocalDate.parse(make_date, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(0, 0)
            var expireDate : LocalDateTime? = null
            if (basicProduct.expirationDateManagementYn == "Y" && basicProduct.expirationDateInfo?.expirationDateWriteYn == "Y"){
                expireDate = makeDate.plusDays(basicProduct.expirationDateInfo!!.expirationDate?.toLong()!!)
            }
            return ExpireMakeDate(expireDate, makeDate)
        }

        return ExpireMakeDate()
    }

    private fun convertExpireDate(basicProduct: BasicProduct) : LocalDateTime?{
        if (basicProduct.expirationDateManagementYn == "Y") {
            if (expire_date.isNullOrBlank())
                return null

            return LocalDate.parse(expire_date, DateTimeFormatter.ofPattern("yyyyMMdd"))
                .atTime(0, 0)
        }
        return null
    }

    private fun convertMakeDate(basicProduct: BasicProduct) : LocalDateTime?{
        if (basicProduct.expirationDateManagementYn == "Y") {
            if (make_date.isNullOrBlank())
                return null

            return LocalDate.parse(make_date, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(0, 0)
        }
        return null
    }
}