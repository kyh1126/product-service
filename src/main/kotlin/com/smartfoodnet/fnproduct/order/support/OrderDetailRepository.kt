package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.fnproduct.order.entity.OrderDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface OrderDetailRepository: JpaRepository<OrderDetail, Long>, QuerydslPredicateExecutor<OrderDetail> {
}