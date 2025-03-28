package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface CollectedOrderRepository : JpaRepository<CollectedOrder, Long>, CollectedOrderRepositoryCustom{
    fun findAllByPartnerIdAndStatus(partnerId: Long, status: OrderStatus): List<CollectedOrder>?
    fun existsByOrderUniqueKey(uniqueKey: String) : Boolean
    fun findByPartnerIdAndOrderNumber(partnerId: Long, orderNumber: String) : List<CollectedOrder>
    fun countByPartnerIdAndStatusAndUnprocessedIsFalse(partnerId: Long, status: OrderStatus) : Long
    fun countByPartnerIdAndUnprocessedIsTrue(partnerId: Long) : Long
    @EntityGraph(attributePaths = ["confirmProductList", "confirmProductList.basicProduct", "confirmRequestOrder"])
    override fun findAllById(ids: Iterable<Long>): MutableList<CollectedOrder>
}