package com.smartfoodnet.fnproduct.order

import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.order.entity.OrderDetail
import com.smartfoodnet.fnproduct.order.model.OrderDetailCreateModel
import com.smartfoodnet.fnproduct.order.model.OrderDetailModel
import com.smartfoodnet.fnproduct.order.support.OrderDetailRepository
import com.smartfoodnet.fnproduct.store.StoreProductService
import org.springframework.data.domain.Pageable
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

    fun getOrderDetail(
        condition: PredicateSearchCondition,
        page: Pageable
    ): PageResponse<OrderDetailModel> {
        return orderDetailRepository.findAll(condition.toPredicate(), page)
            .map(OrderDetailModel::from)
            .run { PageResponse.of(this) }
    }

    private fun convert(orderDetailModel: OrderDetailCreateModel): OrderDetail {
        val orderDetail: OrderDetail = orderDetailModel.toEntity()

        val storeProduct = storeProductService.getStoreProductForOrderDetail(orderDetailModel.partnerId, orderDetailModel.storeProductCode)
        orderDetail.storeProduct = storeProduct

        return orderDetailRepository.save(orderDetail)
    }
}