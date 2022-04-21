package com.smartfoodnet.fnproduct.claim.support

import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.claim.entity.Claim
import com.smartfoodnet.fnproduct.claim.entity.QClaim.claim
import com.smartfoodnet.fnproduct.claim.entity.QExchangeRelease.exchangeRelease
import com.smartfoodnet.fnproduct.claim.entity.QReturnInfo.returnInfo
import com.smartfoodnet.fnproduct.claim.support.condition.ClaimSearchCondition
import com.smartfoodnet.fnproduct.release.entity.QReleaseInfo.releaseInfo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class ClaimRepositoryImpl : Querydsl4RepositorySupport(Claim::class.java), ClaimCustom {
    override fun findAllByCondition(condition: ClaimSearchCondition, page: Pageable): Page<Claim> {
        return applyPagination(page) {
            it.selectFrom(claim)
                .innerJoin(claim.returnInfo, returnInfo).fetchJoin()
                .leftJoin(claim.exchangeRelease, exchangeRelease).fetchJoin()
                .innerJoin(claim.releaseInfo, releaseInfo).fetchJoin()
                .where(
                    condition.toPredicate()
                )
        }
    }

    override fun findAllByCondition(condition: ClaimSearchCondition): List<Claim> {
        return selectFrom(claim)
                .innerJoin(claim.returnInfo, returnInfo).fetchJoin()
                .leftJoin(claim.exchangeRelease, exchangeRelease).fetchJoin()
                .innerJoin(claim.releaseInfo, releaseInfo).fetchJoin()
                .where(
                    condition.toPredicate()
                ).fetch()

    }
}