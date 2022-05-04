package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import org.springframework.data.jpa.repository.JpaRepository

interface ReleaseInfoRepository : JpaRepository<ReleaseInfo, Long>, ReleaseInfoCustom {
    fun findByOrderId(orderId: Long): List<ReleaseInfo>
    fun findByOrderCode(orderCode: String): List<ReleaseInfo>
    fun findByReleaseId(releaseId: Long): ReleaseInfo?
    fun findByReleaseCode(releaseCode: String): ReleaseInfo?
}
