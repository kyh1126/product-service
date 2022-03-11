package com.smartfoodnet.fninventory.inbound

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Projections
import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fninventory.inbound.entity.Inbound
import com.smartfoodnet.fninventory.inbound.entity.InboundExpectedDetail
import com.smartfoodnet.fninventory.inbound.entity.QInbound.inbound
import com.smartfoodnet.fninventory.inbound.entity.QInboundActualDetail.inboundActualDetail
import com.smartfoodnet.fninventory.inbound.entity.QInboundExpectedDetail.inboundExpectedDetail
import com.smartfoodnet.fninventory.inbound.model.dto.*
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import com.smartfoodnet.fninventory.inbound.model.vo.InboundStatusType
import com.smartfoodnet.fninventory.inbound.model.vo.ProductSearchType.*
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.time.LocalTime

class InboundRepositoryImpl
    : InboundRepositoryCustom, Querydsl4RepositorySupport(Inbound::class.java) {

    override fun findInbounds(
        condition: InboundSearchCondition,
        page: Pageable
    ): Page<GetInboundParent> {
        return applyPagination(page) {
            it.from(inbound)
                .innerJoin(inbound.expectedList, inboundExpectedDetail)
                .leftJoin(inboundExpectedDetail.basicProduct, basicProduct)
                .where(
                    condition.toPredicate()
                )
                .select(
                    QGetInboundParent(
                        inbound.id,
                        inboundExpectedDetail.id,
                        inbound.createdAt,
                        inbound.status,
                        inbound.registrationNo,
                        inbound.registrationId,
                        basicProduct.id,
                        basicProduct.code,
                        basicProduct.name,
                        inboundExpectedDetail.method,
                        inbound.expectedDate,
                        inboundExpectedDetail.requestQuantity
                    )
                )
        }
    }

    override fun findStatusExpectedInbounds(basicDate: LocalDateTime): List<Inbound> {
        return selectFrom(inbound)
            .where(
                inbound.status.eq(InboundStatusType.EXPECTED),
                inbound.expectedDate.between(basicDate.with(LocalTime.MIN), basicDate.with(LocalTime.MAX))
            ).fetch()
    }

    override fun findSumActualDetail(expectedIds: List<Long>): List<GetInboundSumDetail> {
        return queryFactory.from(inboundActualDetail)
            .innerJoin(inboundActualDetail.inboundExpectedDetail, inboundExpectedDetail)
            .where(
                inboundExpectedDetail.id.`in`(expectedIds)
            )
            .groupBy(inboundExpectedDetail.id)
            .select(
                QGetInboundSumDetail(
                    inboundExpectedDetail.id,
                    inboundActualDetail.actualInboundDate.max(),
                    inboundActualDetail.actualInboundDate.count(),
                    inboundActualDetail.actualQuantity.sum(),
                    inboundActualDetail.boxQuantity.sum(),
                    inboundActualDetail.palletQuantity.sum(),
                    inboundActualDetail.manufactureDate.max(),
                    inboundActualDetail.manufactureDate.count(),
                    inboundActualDetail.expirationDate.max(),
                    inboundActualDetail.expirationDate.count()
                )
            ).fetch()
    }

    override fun findInboundExpectedWithBasicProduct(
        receivingPlanId: Long,
        shippingProductId: Long
    ): InboundExpectedDetail? {
        return selectFrom(inboundExpectedDetail)
            .leftJoin(inboundExpectedDetail.inbound, inbound).fetchJoin()
            .leftJoin(inboundExpectedDetail.basicProduct, basicProduct).fetchJoin()
            .where(
                inbound.registrationId.eq(receivingPlanId),
                basicProduct.shippingProductId.eq(shippingProductId)
            ).fetchOne()
    }
}