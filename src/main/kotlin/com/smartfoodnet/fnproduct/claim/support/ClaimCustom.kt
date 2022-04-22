package com.smartfoodnet.fnproduct.claim.support

import com.smartfoodnet.fnproduct.claim.entity.Claim
import com.smartfoodnet.fnproduct.claim.support.condition.ClaimSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ClaimCustom {
    fun findAllByCondition(condition: ClaimSearchCondition, page: Pageable): Page<Claim>
    fun findAllByCondition(condition: ClaimSearchCondition): List<Claim>
}