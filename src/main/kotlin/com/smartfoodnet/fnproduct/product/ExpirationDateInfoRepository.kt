package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.ExpirationDateInfo
import org.springframework.data.jpa.repository.JpaRepository

interface ExpirationDateInfoRepository : JpaRepository<ExpirationDateInfo, Long>
