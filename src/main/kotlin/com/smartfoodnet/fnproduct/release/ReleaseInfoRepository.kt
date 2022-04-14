package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.vo.ReleaseStatus
import com.smartfoodnet.fnproduct.release.model.vo.TrackingNumberStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ReleaseInfoRepository : JpaRepository<ReleaseInfo, Long>, ReleaseInfoCustom {
    fun findAllByReleaseStatusIn(
        syncableStatuses: Collection<ReleaseStatus>,
        page: Pageable
    ): Page<ReleaseInfo>

    fun findAllByReleaseStatusInAndPartnerId(
        syncableStatuses: Collection<ReleaseStatus>,
        partnerId: Long,
        page: Pageable
    ): Page<ReleaseInfo>

    fun findByReleaseId(releaseId: Long): ReleaseInfo?
    fun findByOrderId(orderId: Long): List<ReleaseInfo>
    fun findByOrderCode(orderCode: String): List<ReleaseInfo>
    fun findByReleaseCode(releaseCode: String): ReleaseInfo?
    fun findByTrackingNumberStatusAndTrackingNumberIsNotNull(
        trackingNumberStatus: TrackingNumberStatus,
        page: Pageable
    ): Page<ReleaseInfo>
}
