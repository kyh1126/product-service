package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import org.springframework.data.jpa.repository.JpaRepository

interface ConfirmOrderRepository : JpaRepository<ConfirmOrder, Long>, ConfirmOrderRepositoryCustom {
    fun findByOrderId(orderId: Long): ConfirmOrder?
}
