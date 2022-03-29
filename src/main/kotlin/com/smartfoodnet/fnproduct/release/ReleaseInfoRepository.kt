package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.vo.ReleaseStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ReleaseInfoRepository : JpaRepository<ReleaseInfo, Long>, ReleaseInfoCustom {
    fun findAllByReleaseStatusIn(syncableStatuses: Collection<ReleaseStatus>, page: Pageable): Page<ReleaseInfo>
}
