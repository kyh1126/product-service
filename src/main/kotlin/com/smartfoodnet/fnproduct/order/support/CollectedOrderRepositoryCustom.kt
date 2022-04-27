package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fninventory.shortage.model.ShortageOrderProjectionModel
import com.smartfoodnet.fninventory.shortage.support.ProductShortageSearchCondition
import com.smartfoodnet.fnproduct.order.dto.CollectedOrderModel
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.vo.OrderStatus

interface CollectedOrderRepositoryCustom {
    fun findAllByPartnerIdAndStatusGroupByProductId(partnerId:Long, status: OrderStatus, condition: ProductShortageSearchCondition): List<ShortageOrderProjectionModel>
    fun getCountByProductIdAndStatusGroupByProductId(productId:Long, status: OrderStatus): Int?
    fun findAllCollectedOrders(condition: PredicateSearchCondition): List<CollectedOrderModel>
    fun findMissingAffectedOrders(partnerId: Long, basicProductId: Long) : List<CollectedOrder>
}