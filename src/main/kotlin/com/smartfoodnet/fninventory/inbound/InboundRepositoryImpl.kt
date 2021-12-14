package com.smartfoodnet.fninventory.inbound

import com.querydsl.core.BooleanBuilder
import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fninventory.inbound.entity.Inbound
import com.smartfoodnet.fninventory.inbound.entity.QInbound.inbound
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import com.smartfoodnet.fninventory.inbound.model.vo.ProductSearchType.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class InboundRepositoryImpl : InboundCustom, Querydsl4RepositorySupport(Inbound::class.java) {
    override fun findByPartnerId(condition: InboundSearchCondition, page: Pageable): Page<Inbound> {
        return applyPagination(page) {
            it.selectFrom(inbound)
                .join(inbound.basicProduct).fetchJoin()
                .where(
                    // 고객사 ID는 필수
                    inbound.partnerId.eq(condition.partnerId),
                    // 입고 상태
                    inbound.status.eq(condition.statusType),
                    // 일자 필수(년월일 ~ 년월일)
                    inbound.createdAt.between(condition.fromDate, condition.toDate),
                    // 검색조건에 따라 조건 필드가 달라짐
                    // 입고등록번호, 기본상품명, 기본상품코드
                    productSearchPredicate(condition)
                )
        }
    }
}

fun productSearchPredicate(inboundSearchCondition: InboundSearchCondition): BooleanBuilder {
    val builder = BooleanBuilder()

    if (!inboundSearchCondition.keyword.isNullOrBlank()) {
        when (inboundSearchCondition.productSearchType) {
            BASIC_PRODUCT_CODE -> builder.and(inbound.basicProduct.code.contains(inboundSearchCondition.keyword))
            BASIC_PRODUCT_NAME -> builder.and(inbound.basicProduct.name.contains(inboundSearchCondition.keyword))
            INBOUND_REGISTRATION_NO -> builder.and(inbound.registrationNo.contains(inboundSearchCondition.keyword))
            else -> builder
        }
    }

    return builder
}