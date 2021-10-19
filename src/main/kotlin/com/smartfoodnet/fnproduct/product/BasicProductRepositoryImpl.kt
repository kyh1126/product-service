package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class BasicProductRepositoryImpl : Querydsl4RepositorySupport(BasicProduct::class.java), BasicProductCustom {

    override fun findByPartnerIdAndType(partnerId: Long?, type: BasicProductType?, page: Pageable): Page<BasicProduct> {
        return applyPagination(page) {
            it.selectFrom(basicProduct)
                .where(eqPartnerId(partnerId), eqType(type))
        }
    }

    override fun countByPartnerIdAndTypeIn(partnerId: Long?, types: Collection<BasicProductType>): Long {
        return selectFrom(basicProduct)
            .where(eqPartnerId(partnerId), inType(types))
            .fetchCount()
    }

    private fun eqPartnerId(partnerId: Long?) = if (partnerId == null) null else basicProduct.partnerId.eq(partnerId)

    private fun eqType(type: BasicProductType?) = if (type == null) null else basicProduct.type.eq(type)

    private fun inType(types: Collection<BasicProductType>) = basicProduct.type.`in`(types)
}
