package com.smartfoodnet.fnproduct.product.mapper

import com.smartfoodnet.fnproduct.partner.PartnerService
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 상품코드 채번 규칙<p>
 *
 * @see <a href="https://docs.google.com/spreadsheets/d/1z1vAZCbcleMM0v5nDbJl-aER0ExCgMx_50-obAxsd0c/edit#gid=1591663723">상품코드채번규칙</a>
 */
@Component
@Transactional(readOnly = true)
class BasicProductCodeGenerator(
    private val partnerService: PartnerService,
    private val basicProductRepository: BasicProductRepository,
) {
    fun getBasicProductCode(
        partnerId: Long,
        typeCode: String?,
        temperatureCode: String?,
    ): String {
        val customerNumber = partnerService.getPartner(partnerId).customerNumber
        val basicProductCodeTypes = setOf(BasicProductType.BASIC, BasicProductType.PACKAGE)

        // 00001 부터 시작
        val totalProductCount = basicProductRepository.countByPartnerIdAndTypeIn(partnerId, basicProductCodeTypes)
            .run { String.format("%05d", this + 1) }

        return customerNumber + typeCode + temperatureCode + totalProductCount
    }
}
