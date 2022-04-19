package com.smartfoodnet.config.querydsl.core

import com.querydsl.core.DefaultQueryMetadata
import com.querydsl.core.types.Predicate

/**
 * JPA 2.1 부터 지원되는 JOIN ON 의 ON 에서 null arguments 스킵 기능 추가된 버전
 * <p>
 * @see     com.querydsl.core.support.QueryMixin.on(com.querydsl.core.types.Predicate...)
 * @see     com.querydsl.core.DefaultQueryMetadata.addJoinCondition(com.querydsl.core.types.Predicate)
 */
class CustomQueryMetadata : DefaultQueryMetadata() {
    override fun addJoinCondition(o: Predicate?) {
        if (o == null) {
            return
        }
        super.addJoinCondition(o)
    }
}
