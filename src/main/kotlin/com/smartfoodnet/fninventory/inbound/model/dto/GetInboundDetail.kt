package com.smartfoodnet.fninventory.inbound.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.querydsl.core.annotations.QueryProjection
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fninventory.inbound.entity.InboundActualDetail
import com.smartfoodnet.fninventory.inbound.entity.InboundExpectedDetail
import com.smartfoodnet.fninventory.inbound.model.vo.InboundMethodType
import com.smartfoodnet.fninventory.inbound.model.vo.InboundStatusType
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import java.time.LocalDateTime

data class InboundBasicProductModel(
    val basicProductId : Long,
    val basicProductName : String,
    val basicProductCode : String
){
    companion object{
        fun toDto(basicProduct: BasicProduct): InboundBasicProductModel{
            return basicProduct.run {
                InboundBasicProductModel(
                    basicProductId = id!!,
                    basicProductName = name!!,
                    basicProductCode = code!!
                )
            }
        }
    }
}

data class GetInboundDetailModel(
    val inboundId: Long,
    val partnerId: Long,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val expectedDate: LocalDateTime,
    val registrationNo: String? = null,
    val registrationId: Long? = null,
    val memo: String? = null,
    val status: InboundStatusType,
    val basicProduct : InboundBasicProductModel,
    val expectedDetailId : Long,
    val requestQuantity: Long,
    val method: InboundMethodType? = null,
    val deliveryFlag: Boolean = false,
    val deliveryName: String? = null,
    val trackingNo: String? = null,
    val inboundActualList: List<InboundActualModel>,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val createdAt: LocalDateTime
){
    companion object {
        fun toDto(inboundExpectedDetail: InboundExpectedDetail) : GetInboundDetailModel {
            val inbound = inboundExpectedDetail.inbound!!

            return GetInboundDetailModel(
                inbound.id!!,
                inbound.partnerId,
                inbound.expectedDate,
                inbound.registrationNo,
                inbound.registrationId,
                inbound.memo,
                inbound.status,
                basicProduct = InboundBasicProductModel.toDto(inboundExpectedDetail.basicProduct),
                inboundExpectedDetail.id!!,
                requestQuantity = inboundExpectedDetail.requestQuantity,
                method = inboundExpectedDetail.method,
                deliveryFlag = inboundExpectedDetail.deliveryFlag,
                deliveryName = inboundExpectedDetail.deliveryName,
                trackingNo = inboundExpectedDetail.trackingNo,
                inboundActualList = inboundExpectedDetail.inboundActualDetail?.map{ InboundActualModel.toDto(it) }?: listOf(),
                createdAt = inbound.createdAt!!
            )
        }
    }
}

data class InboundActualModel(
    val actualDetailId: Long,
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val actualInboundDate: LocalDateTime,
    val actualQuantity: Long,
    val boxQuantity: Long? = null,
    val palletQuantity: Long? = null,
    val manufactureDate: LocalDateTime? = null,
    val expirationDate: LocalDateTime? = null
){
    companion object{
        fun toDto(inboundActualDetail : InboundActualDetail) : InboundActualModel{
            return inboundActualDetail.run {
                InboundActualModel(
                    actualDetailId = id!!,
                    actualInboundDate!!,
                    actualQuantity!!,
                    boxQuantity,
                    palletQuantity,
                    manufactureDate,
                    expirationDate
                )
            }
        }
    }
}
