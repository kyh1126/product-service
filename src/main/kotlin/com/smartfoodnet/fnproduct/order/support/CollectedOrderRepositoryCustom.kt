package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fninventory.shortage.model.ShortageOrderProjectionModel
import com.smartfoodnet.fnproduct.order.dto.CollectedOrderModel
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.model.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CollectedOrderRepositoryCustom {
    fun findAllByPartnerIdAndStatusGroupByProductId(partnerId:Long, status: OrderStatus): List<ShortageOrderProjectionModel>
    fun getCountByProductIdAndStatusGroupByProductId(productId:Long, status: OrderStatus): Int?
    fun findCollectedOrdersWithPageable(condition: PredicateSearchCondition, pagination: Pageable): Page<CollectedOrderModel>
    fun findCollectedOrders(condition: PredicateSearchCondition): List<CollectedOrderModel>
}