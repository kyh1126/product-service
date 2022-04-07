package com.smartfoodnet.fnproduct.claim.support

import com.smartfoodnet.fnproduct.claim.entity.ExchangeRelease
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface ExchangeReleaseRepository: JpaRepository<ExchangeRelease, Long>, QuerydslPredicateExecutor<ExchangeRelease> {
}