package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.request.ReleaseInfoSearchCondition
import com.smartfoodnet.fnproduct.release.model.vo.ReleaseStatus
import com.smartfoodnet.fnproduct.release.model.vo.TrackingNumberStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ReleaseInfoCustom {
    fun findAllByCondition(condition: ReleaseInfoSearchCondition, page: Pageable): Page<ReleaseInfo>

    fun findAllByReleaseStatuses(
        partnerId: Long? = null,
        deliveryAgencyId: Long? = null,
        releaseStatuses: Collection<ReleaseStatus>,
        page: Pageable
    ): Page<ReleaseInfo>

    fun findAllByTrackingNumberStatus(
        trackingNumberStatus: TrackingNumberStatus,
        checkTrackingNumber: Boolean,
        page: Pageable
    ): Page<ReleaseInfo>
}
