package com.smartfoodnet.fnproduct.claim.support

import com.smartfoodnet.fnproduct.claim.entity.Claim
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface ClaimRepository: JpaRepository<Claim, Long>, QuerydslPredicateExecutor<Claim> {
}