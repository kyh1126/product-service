package com.smartfoodnet.fnproduct.product.mapper

import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fnproduct.product.BasicProductCodeSeqRepository
import com.smartfoodnet.fnproduct.product.entity.BasicProductCodeSeq
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * 상품코드 채번 규칙
 * <p>
 * @see <a href="https://docs.google.com/spreadsheets/d/1z1vAZCbcleMM0v5nDbJl-aER0ExCgMx_50-obAxsd0c/edit#gid=1591663723">상품코드채번규칙</a>
 */
@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
class BasicProductCodeGenerator(
    private val basicProductCodeSeqRepository: BasicProductCodeSeqRepository,
) {
    private val basicProductCodeTypes =
        setOf(BasicProductType.BASIC, BasicProductType.PACKAGE, BasicProductType.CUSTOM_SUB)

    private val defaultCount = 1
    private val partnerCodeLength = 4

    fun getBasicProductCode(
        partnerId: Long,
        partnerCode: String,
        type: BasicProductType,
        handlingTemperature: HandlingTemperatureType,
    ): String? {
        if (validateNotAvailableType(type)
            || validateNotAvailableTemperatureType(type, handlingTemperature)
        ) return null

        // 00001 부터 시작
        val basicProductCodeSeq =
            when (updateSucceeded(partnerId)) {
                true -> basicProductCodeSeqRepository.findById(partnerId).get()
                false -> basicProductCodeSeqRepository.save(BasicProductCodeSeq.initial(partnerId))
            }
        val totalProductCount = String.format("%05d", basicProductCodeSeq.seq)
        return getCustomerNumber(partnerCode) + type.code + handlingTemperature.code + totalProductCount
    }

    private fun updateSucceeded(partnerId: Long) =
        basicProductCodeSeqRepository.updateSeq(partnerId, defaultCount) == 1

    private fun getCustomerNumber(partnerCode: String): String {
        if (partnerCode.length != partnerCodeLength) {
            log.error("[BasicProductCodeGenerator] 상품코드 채번 에러, partnerCode = $partnerCode")
            throw IllegalArgumentException("partnerCode 는 4자리여야 합니다.")
        }
        return partnerCode
    }

    private fun validateNotAvailableType(type: BasicProductType): Boolean {
        return type !in basicProductCodeTypes
    }

    private fun validateNotAvailableTemperatureType(
        type: BasicProductType,
        handlingTemperature: HandlingTemperatureType
    ) = type != BasicProductType.PACKAGE && handlingTemperature == HandlingTemperatureType.MIX

    companion object : Log
}
