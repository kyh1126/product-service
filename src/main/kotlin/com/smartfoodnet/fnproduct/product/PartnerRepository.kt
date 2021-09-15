package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.Partner
import org.springframework.data.jpa.repository.JpaRepository

interface PartnerRepository : JpaRepository<Partner, Long>
