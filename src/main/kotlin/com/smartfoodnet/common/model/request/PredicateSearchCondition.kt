package com.smartfoodnet.common.model.request

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.error.exception.ErrorCode.SEARCH_CONDITION_ILLEGAL_ARGUMENT

abstract class PredicateSearchCondition {
    fun toPredicate(): Predicate {
        try {
            return assemblePredicate(BooleanBuilder())
        } catch (e: IllegalArgumentException) {
            throw BaseRuntimeException(
                errorCode = SEARCH_CONDITION_ILLEGAL_ARGUMENT,
                errorMessage = listOf(SEARCH_CONDITION_ILLEGAL_ARGUMENT.errorMessage, e.message)
                    .joinToString(" ")
            )
        }
    }

    protected abstract fun assemblePredicate(predicate: BooleanBuilder): Predicate
}
