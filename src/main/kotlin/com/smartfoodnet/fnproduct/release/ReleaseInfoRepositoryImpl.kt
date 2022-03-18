package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.order.entity.QCollectedOrder.collectedOrder
import com.smartfoodnet.fnproduct.order.model.OrderStatus
import com.smartfoodnet.fnproduct.release.entity.QReleaseInfo.releaseInfo
import com.smartfoodnet.fnproduct.release.entity.QReleaseOrderMapping.releaseOrderMapping
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.request.ReleaseInfoSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class ReleaseInfoRepositoryImpl : Querydsl4RepositorySupport(ReleaseInfo::class.java), ReleaseInfoCustom {
    override fun findAllByCondition(condition: ReleaseInfoSearchCondition, page: Pageable): Page<ReleaseInfo> {
        return applyPagination(page) {
            it.selectFrom(releaseInfo)
                .innerJoin(releaseInfo.releaseOrderMappings, releaseOrderMapping)
                .innerJoin(releaseOrderMapping.collectedOrder, collectedOrder)
                .where(
                    containsOrderNumber(condition.orderNumber),
                    containsStoreName(condition.storeName),
                    eqOrderStatus(condition.orderStatus),
                    eqReceiverName(condition.receiverName),
                    eqUploadType(condition.uploadType),
                    eqClaimStatus(condition.claimStatus),
                    containsOrderCode(condition.orderCode),
                    containsShippingCode(condition.shippingCode),
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

    private fun eqUploadType(uploadType: String?) =
        uploadType?.let { collectedOrder.uploadType.eq(it) }

    private fun eqClaimStatus(claimStatus: String?) =
        claimStatus?.let { collectedOrder.claimStatus.eq(it) }

    private fun containsOrderCode(orderCode: String?) =
        orderCode?.let { releaseInfo.orderCode.contains(it) }

    private fun containsShippingCode(shippingCode: String?) =
        shippingCode?.let { releaseInfo.shippingCode.contains(it) }
}
