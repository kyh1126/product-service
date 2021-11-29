package com.smartfoodnet.fnproduct.order

import com.smartfoodnet.fnproduct.order.entity.OrderDetail
import com.smartfoodnet.fnproduct.order.model.OrderDetailCreateModel
import com.smartfoodnet.fnproduct.order.model.OrderDetailModel
import com.smartfoodnet.fnproduct.order.support.OrderDetailRepository
import com.smartfoodnet.fnproduct.store.StoreProductService
import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.streams.toList

@Service
@Transactional(readOnly = true)
class OrderService(
        private val orderDetailRepository: OrderDetailRepository,
        private val storeProductService: StoreProductService
) {

    @Transactional
    fun createOrderDetail(orderDetailModels: List<OrderDetailCreateModel>): List<OrderDetailModel> {
        val orderDetails = orderDetailModels.stream().map { convert(it) }.toList()

        return orderDetails.map{OrderDetailModel.from (it)}
    }

    private fun convert(orderDetailModel: OrderDetailCreateModel): OrderDetail {
        val orderDetail: OrderDetail = orderDetailModel.toEntity()

        val storeProduct: StoreProduct = storeProductService.getStoreProductForOrderDetail(orderDetailModel.partnerId, orderDetailModel.storeProductCode)
        orderDetail.storeProduct = storeProduct

        return orderDetailRepository.save(orderDetail)
    }
}