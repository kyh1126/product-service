package com.smartfoodnet.fninventory.inbound

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Projections
import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fninventory.inbound.entity.Inbound
import com.smartfoodnet.fninventory.inbound.entity.InboundExpectedDetail
import com.smartfoodnet.fninventory.inbound.entity.QInbound.inbound
import com.smartfoodnet.fninventory.inbound.entity.QInboundActualDetail.inboundActualDetail
import com.smartfoodnet.fninventory.inbound.entity.QInboundExpectedDetail.inboundExpectedDetail
import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundActualDetail
import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundSumDetail
import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundParent
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import com.smartfoodnet.fninventory.inbound.model.vo.ProductSearchType.*
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

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
                    // 고객사 ID는 필수
                    inbound.partnerId.eq(condition.partnerId),
                    // 일자 필수(년월일 ~ 년월일)
                    inbound.createdAt.between(condition.fromDate, condition.toDate),
                    // 검색조건에 따라 조건 필드가 달라짐
                    // 입고등록번호, 기본상품명, 기본상품코드
                    productSearchPredicate(condition)
                )
                .select(
                    Projections.constructor(
                        GetInboundParent::class.java,
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

    private fun productSearchPredicate(inboundSearchCondition: InboundSearchCondition): BooleanBuilder {

        val builder = BooleanBuilder()

        if (inboundSearchCondition.statusType != null){
            builder.and(inbound.status.eq(inboundSearchCondition.statusType))
        }

        if (!inboundSearchCondition.keyword.isNullOrBlank()) {
            when (inboundSearchCondition.productSearchType) {
                BASIC_PRODUCT_CODE -> builder.and(inboundExpectedDetail.basicProduct.code.contains(inboundSearchCondition.keyword))
                BASIC_PRODUCT_NAME -> builder.and(inboundExpectedDetail.basicProduct.name.contains(inboundSearchCondition.keyword))
                INBOUND_REGISTRATION_NO -> builder.and(inbound.registrationNo.contains(inboundSearchCondition.keyword))
            }
        }

        return builder
    }

    override fun findSumActualDetail(expectedIds: List<Long>): List<GetInboundSumDetail> {
        return queryFactory.from(inboundActualDetail)
            .innerJoin(inboundActualDetail.inboundExpectedDetail, inboundExpectedDetail)
            .where(
                inboundExpectedDetail.id.`in`(expectedIds)
            )
            .groupBy(inboundExpectedDetail.id)
            .select(
                Projections.constructor(
                    GetInboundSumDetail::class.java,
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

    override fun findInboundActualDetail(
        partnerId: Long,
        expectedId: Long
    ): List<GetInboundActualDetail> {
        return queryFactory.from(inbound)
            .innerJoin(inbound.expectedList, inboundExpectedDetail)
            .innerJoin(inboundExpectedDetail.inboundActualDetail, inboundActualDetail)
            .where(
                inbound.partnerId.eq(partnerId),
                inboundExpectedDetail.id.eq(expectedId)
            )
            .select(
                Projections.constructor(
                    GetInboundActualDetail::class.java,
                    inboundActualDetail.id,
                    inboundExpectedDetail.id,
                    inboundActualDetail.actualInboundDate,
                    inboundActualDetail.actualQuantity,
                    inboundActualDetail.boxQuantity,
                    inboundActualDetail.palletQuantity,
                    inboundActualDetail.manufactureDate,
                    inboundActualDetail.expirationDate
                )
            ).fetch()

    }
}