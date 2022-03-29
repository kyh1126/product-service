package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.fnproduct.release.entity.ReleaseProduct
import org.springframework.data.jpa.repository.JpaRepository

interface ReleaseProductRepository : JpaRepository<ReleaseProduct, Long>
