package com.smartfoodnet.fnproduct.product.mapper

import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 상품코드 채번 규칙
 * <p>
 * @see <a href="https://docs.google.com/spreadsheets/d/1z1vAZCbcleMM0v5nDbJl-aER0ExCgMx_50-obAxsd0c/edit#gid=1591663723">상품코드채번규칙</a>
 */
@Component
@Transactional(readOnly = true)
class BasicProductCodeGenerator(
    private val basicProductRepository: BasicProductRepository,
) {
    private val basicProductCodeTypes =
        setOf(BasicProductType.BASIC, BasicProductType.PACKAGE, BasicProductType.CUSTOM_SUB)

    fun getBasicProductCode(
        partnerId: Long?,
        type: BasicProductType,
        handlingTemperature: HandlingTemperatureType?,
    ): String? {
        if (partnerId == null || handlingTemperature == null) return null
        if (validateNotAvailableType(type)
            || validateNotAvailableTemperatureType(type, handlingTemperature)
        ) return null

        val temperatureCode = handlingTemperature.code
        // 00001 부터 시작
        val totalProductCount =
            basicProductRepository.countByPartnerIdAndTypeIn(partnerId, basicProductCodeTypes)
                .run { String.format("%05d", this + 1) }

        return getCustomerNumber(partnerId) + type.code + temperatureCode + totalProductCount
    }

    private fun getCustomerNumber(partnerId: Long): String {
        if (partnerId > 9999L) {
            log.error("[BasicProductCodeGenerator] 상품코드 채번 에러, partnerId = $partnerId")
            throw IllegalArgumentException("partnerId 는 9999 보다 작아야 합니다.")
        }

        return String.format("%04d", partnerId)
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
