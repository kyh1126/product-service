package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.fnproduct.release.entity.ReleaseOrderMapping
import org.springframework.data.jpa.repository.JpaRepository

interface ReleaseOrderMappingRepository : JpaRepository<ReleaseOrderMapping, Long>
