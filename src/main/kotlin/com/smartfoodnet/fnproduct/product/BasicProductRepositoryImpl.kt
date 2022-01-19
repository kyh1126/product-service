package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType

class BasicProductRepositoryImpl : Querydsl4RepositorySupport(BasicProduct::class.java),
    BasicProductCustom {

    override fun countByPartnerIdAndTypeIn(
        partnerId: Long?,
        types: Collection<BasicProductType>
    ): Long {
        return selectFrom(basicProduct)
            .where(eqPartnerId(partnerId), inType(types))
            .fetchCount()
    }

    private fun eqPartnerId(partnerId: Long?) = partnerId?.let { basicProduct.partnerId.eq(it) }

    private fun inType(types: Collection<BasicProductType>) = basicProduct.type.`in`(types)

    override fun getPartnerIdsFromBasicProduct(expirationDateManagementYn: String, activeYn: String): List<Long>? {
        return select(basicProduct.partnerId)
            .from(basicProduct)
            .where(
                basicProduct.expirationDateManagementYn.eq(expirationDateManagementYn),
                basicProduct.activeYn.eq(activeYn),
                basicProduct.partnerId.isNotNull
            )
            .groupBy(basicProduct.partnerId)
            .fetch()
    }

    override fun getPartnerIdsFromBasicProduct(activeYn: String): List<Long>? {
        return select(basicProduct.partnerId)
            .from(basicProduct)
            .where(basicProduct.activeYn.eq(activeYn), basicProduct.partnerId.isNotNull)
            .groupBy(basicProduct.partnerId)
            .fetch()
    }
}
