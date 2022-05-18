package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fninventory.shortage.model.ShortageOrderProjectionModel
import com.smartfoodnet.fninventory.shortage.support.ProductShortageSearchCondition
import com.smartfoodnet.fnproduct.order.dto.CollectedOrderFlatModel
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CollectedOrderRepositoryCustom {
    fun findAllByPartnerIdAndStatusGroupByProductId(partnerId:Long, status: OrderStatus, condition: ProductShortageSearchCondition): List<ShortageOrderProjectionModel>
    fun getCountByProductIdAndStatusGroupByProductId(productId:Long, status: OrderStatus): Int?
    fun findCollectedOrders(condition: PredicateSearchCondition, page: Pageable): Page<CollectedOrderFlatModel>
    fun findMissingAffectedOrders(partnerId: Long, basicProductId: Long) : List<CollectedOrder>
}