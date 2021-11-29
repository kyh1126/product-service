package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.fnproduct.order.entity.OrderDetail
import org.springframework.data.jpa.repository.JpaRepository

interface OrderDetailRepository: JpaRepository<OrderDetail, Long> {
}