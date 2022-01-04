package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.fninventory.shortage.model.ShortageOrderProjectionModel
import com.smartfoodnet.fnproduct.order.model.OrderStatus

interface OrderDetailCustom {
    fun findAllByPartnerIdAndStatusGroupByProductId(partnerId:Long, status: OrderStatus): List<ShortageOrderProjectionModel>
    fun getCountByProductIdAndStatusGroupByProductId(productId:Long, status: OrderStatus): Int?
}