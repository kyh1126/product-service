package com.smartfoodnet.fnproduct.order

import com.smartfoodnet.fnproduct.order.model.OrderDetailModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderService {

    @Transactional
    fun createOrderDetail(orderDetailModel: OrderDetailModel): OrderDetailModel {
        return orderDetailModel
    }
}