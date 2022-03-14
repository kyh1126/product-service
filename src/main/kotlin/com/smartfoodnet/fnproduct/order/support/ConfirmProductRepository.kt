package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import org.springframework.data.jpa.repository.JpaRepository

interface ConfirmProductRepository : JpaRepository<ConfirmOrder, Long>