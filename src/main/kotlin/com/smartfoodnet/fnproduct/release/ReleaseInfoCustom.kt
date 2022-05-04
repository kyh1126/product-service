package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.request.ReleaseInfoSearchCondition
import com.smartfoodnet.fnproduct.release.model.request.ReleaseStatusSearchCondition
import com.smartfoodnet.fnproduct.release.model.vo.TrackingNumberStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ReleaseInfoCustom {
    fun findAllByCondition(condition: ReleaseInfoSearchCondition, page: Pageable): Page<ReleaseInfo>
    fun findAllByReleaseStatuses(condition: ReleaseStatusSearchCondition): List<ReleaseInfo>
    fun findAllByReleaseStatuses(condition: ReleaseStatusSearchCondition, page: Pageable): Page<ReleaseInfo>
    fun findAllByTrackingNumberStatus(
        trackingNumberStatus: TrackingNumberStatus,
        checkTrackingNumber: Boolean,
        page: Pageable
    ): Page<ReleaseInfo>
}
