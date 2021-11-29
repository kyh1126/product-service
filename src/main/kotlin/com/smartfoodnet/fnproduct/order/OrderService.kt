package com.smartfoodnet.fnproduct.order

import com.google.common.collect.Lists
import com.smartfoodnet.fnproduct.order.entity.OrderDetail
import com.smartfoodnet.fnproduct.order.model.OrderDetailCreateModel
import com.smartfoodnet.fnproduct.order.model.OrderDetailModel
import com.smartfoodnet.fnproduct.order.support.OrderDetailRepository
import com.smartfoodnet.fnproduct.store.support.StoreProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.streams.toList

@Service
@Transactional(readOnly = true)
class OrderService(
        private val orderDetailRepository: OrderDetailRepository
) {

    @Transactional
    fun createOrderDetail(orderDetailModels: List<OrderDetailCreateModel>): List<OrderDetailModel> {
        val orderDetails = orderDetailModels.stream().map { toEntity(it) }.toList()

        return orderDetails.map{OrderDetailModel.from (it)}
    }

    private fun toEntity(orderDetailModel: OrderDetailCreateModel): OrderDetail {
        return orderDetailRepository.save(orderDetailModel.toEntity())
    }
}