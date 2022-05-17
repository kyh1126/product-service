package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.ShippingProductArchive
import org.springframework.data.jpa.repository.JpaRepository

interface ShippingProductArchiveRepository : JpaRepository<ShippingProductArchive, Long>
