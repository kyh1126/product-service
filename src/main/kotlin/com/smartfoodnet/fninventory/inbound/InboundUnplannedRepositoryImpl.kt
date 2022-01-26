package com.smartfoodnet.fninventory.inbound

import com.querydsl.core.types.Predicate
import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fninventory.inbound.entity.InboundUnplanned
import com.smartfoodnet.fninventory.inbound.entity.QInboundUnplanned.inboundUnplanned
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class InboundUnplannedRepositoryImpl : InboundUnplannedRepositoryCustom, Querydsl4RepositorySupport(InboundUnplanned::class.java) {
    override fun findInboundUnplanned(
        condition: InboundSearchCondition,
        page: Pageable
    ): Page<InboundUnplanned> {
        return applyPagination(page){
            it.selectFrom(inboundUnplanned)
                .innerJoin(inboundUnplanned.basicProduct, basicProduct)
                .where(
                    condition.toPredicate()
                )
        }
    }
}