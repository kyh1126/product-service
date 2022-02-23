package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.entity.OrderDetail
import com.smartfoodnet.fnproduct.order.model.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface CollectedOrderRepository : JpaRepository<CollectedOrder, Long>,
    QuerydslPredicateExecutor<CollectedOrder>, CollectedOrderRepositoryCustom{
    fun findAllByPartnerIdAndStatus(partnerId: Long, status: OrderStatus): List<CollectedOrder>?
}