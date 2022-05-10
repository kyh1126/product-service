package com.smartfoodnet.fnproduct.dashbaord

import com.smartfoodnet.fnproduct.dashbaord.model.CollectedOrderCount
import com.smartfoodnet.fnproduct.dashbaord.model.DashboardModel
import com.smartfoodnet.fnproduct.order.support.CollectedOrderRepository
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import org.springframework.stereotype.Service

@Service
class DashboardService(
    private val collectedOrderRepository: CollectedOrderRepository
) {
    fun getCount(partnerId: Long): DashboardModel{

        return DashboardModel(
            getCollectedInfo(partnerId)
        )
    }

    private fun getCollectedInfo(partnerId : Long) : CollectedOrderCount{
        return CollectedOrderCount(
            collectedOrderRepository.countByPartnerIdAndStatusAndUnprocessedIsFalse(partnerId, OrderStatus.NEW),
            collectedOrderRepository.countByPartnerIdAndUnprocessedIsTrue(partnerId),
            collectedOrderRepository.countByPartnerIdAndStatusAndUnprocessedIsFalse(partnerId, OrderStatus.CANCEL)
        )
    }
}
