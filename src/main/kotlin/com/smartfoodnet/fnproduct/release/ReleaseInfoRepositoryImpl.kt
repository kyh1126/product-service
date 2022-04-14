package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimStatus
import com.smartfoodnet.fnproduct.order.entity.QCollectedOrder.collectedOrder
import com.smartfoodnet.fnproduct.order.entity.QConfirmOrder.confirmOrder
import com.smartfoodnet.fnproduct.order.entity.QConfirmRequestOrder.confirmRequestOrder
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import com.smartfoodnet.fnproduct.release.entity.QReleaseInfo.releaseInfo
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.request.ReleaseInfoSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class ReleaseInfoRepositoryImpl : Querydsl4RepositorySupport(ReleaseInfo::class.java), ReleaseInfoCustom {
    override fun findAllByCondition(condition: ReleaseInfoSearchCondition, page: Pageable): Page<ReleaseInfo> {
        return applyPagination(page) {
            it.selectFrom(releaseInfo)
                .innerJoin(releaseInfo.confirmOrder, confirmOrder)
                .innerJoin(confirmOrder.requestOrderList, confirmRequestOrder)
                .innerJoin(confirmRequestOrder.collectedOrder, collectedOrder)
                .where(
                    containsOrderNumber(condition.orderNumber),
                    containsStoreName(condition.storeName),
                    eqOrderStatus(condition.orderStatus),
                    eqReceiverName(condition.receiverName),
                    eqUploadType(condition.uploadType),
                    eqClaimStatus(condition.claimStatus),
                    eqPartnerId(condition.partnerId),
                    containsOrderCode(condition.orderCode),
                    containsTrackingNumber(condition.trackingNumber),
                )
                .groupBy(releaseInfo)
        }
    }

    private fun containsOrderNumber(orderNumber: String?) =
        orderNumber?.let { collectedOrder.orderNumber.contains(it) }

    private fun containsStoreName(storeName: String?) =
        storeName?.let { collectedOrder.storeName.contains(it) }

    private fun eqOrderStatus(orderStatus: OrderStatus?) =
        orderStatus?.let { collectedOrder.status.eq(it) }

    private fun eqReceiverName(receiverName: String?) =
        receiverName?.let { collectedOrder.receiver.name.eq(it) }

    private fun eqUploadType(uploadType: OrderUploadType?) =
        uploadType?.let { collectedOrder.uploadType.eq(it) }

    private fun eqClaimStatus(claimStatus: ClaimStatus?) =
        claimStatus?.let { collectedOrder.claimStatus.eq(it) }

    private fun eqPartnerId(partnerId: Long?) =
        partnerId?.let { releaseInfo.partnerId.eq(partnerId) }

    private fun containsOrderCode(orderCode: String?) =
        orderCode?.let { releaseInfo.orderCode.contains(it) }

    private fun containsTrackingNumber(trackingNumber: String?) =
        trackingNumber?.let { releaseInfo.trackingNumber.contains(it) }
}
