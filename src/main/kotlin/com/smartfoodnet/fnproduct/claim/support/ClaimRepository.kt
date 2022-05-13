package com.smartfoodnet.fnproduct.claim.support

import com.smartfoodnet.fnproduct.claim.entity.Claim
import org.springframework.data.jpa.repository.JpaRepository

interface ClaimRepository: JpaRepository<Claim, Long>, ClaimCustom {
    fun findByIdAndPartnerId(id: Long, partnerId: Long): Claim?
}