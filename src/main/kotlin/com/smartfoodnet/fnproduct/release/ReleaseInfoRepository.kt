package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import org.springframework.data.jpa.repository.JpaRepository

interface ReleaseInfoRepository : JpaRepository<ReleaseInfo, Long>
