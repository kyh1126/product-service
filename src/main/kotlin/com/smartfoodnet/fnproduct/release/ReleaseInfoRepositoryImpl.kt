package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.claim.entity.QClaim.claim
import com.smartfoodnet.fnproduct.claim.model.vo.ExchangeStatus
import com.smartfoodnet.fnproduct.claim.model.vo.ReturnStatus
import com.smartfoodnet.fnproduct.order.entity.QCollectedOrder.collectedOrder
import com.smartfoodnet.fnproduct.order.entity.QConfirmOrder.confirmOrder
import com.smartfoodnet.fnproduct.order.entity.QConfirmRequestOrder.confirmRequestOrder
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import com.smartfoodnet.fnproduct.release.entity.QReleaseInfo.releaseInfo
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.request.ReleaseInfoSearchCondition
import com.smartfoodnet.fnproduct.release.model.request.ReleaseStatusSearchCondition
import com.smartfoodnet.fnproduct.release.model.vo.ReleaseStatus
import com.smartfoodnet.fnproduct.release.model.vo.TrackingNumberStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class ReleaseInfoRepositoryImpl : Querydsl4RepositorySupport(ReleaseInfo::class.java), ReleaseInfoCustom {
    override fun findAllByCondition(condition: ReleaseInfoSearchCondition, page: Pageable): Page<ReleaseInfo> {
        return applyPagination(page) {
            it.selectFrom(releaseInfo)
                .leftJoin(releaseInfo.claim, claim)
                .innerJoin(releaseInfo.confirmOrder, confirmOrder)
                .innerJoin(confirmOrder.requestOrderList, confirmRequestOrder)
                .innerJoin(confirmRequestOrder.collectedOrder, collectedOrder)
                .where(
                    containsOrderNumber(condition.orderNumber),
                    containsStoreName(condition.storeName),
                    eqReleaseStatus(ReleaseStatus.fromOrderStatus(condition.orderStatus)),
                    eqReceiverName(condition.receiverName),
                    eqUploadType(condition.uploadType),
                    eqReturnStatus(condition.returnStatus),
                    eqExchangeStatus(condition.exchangeStatus),
                    eqPartnerId(condition.partnerId),
                    containsOrderCode(condition.orderCode),
                    containsTrackingNumber(condition.trackingNumber),
                )
                .groupBy(releaseInfo)
        }
    }

    override fun findAllByReleaseStatuses(condition: ReleaseStatusSearchCondition): List<ReleaseInfo> {
        return selectFrom(releaseInfo)
            .where(
                inReleaseStatus(condition.releaseStatuses),
                eqPartnerId(condition.partnerId),
                eqDeliveryAgencyId(condition.deliveryAgencyId),
                eqOrderCode(condition.orderCode),
                eqReleaseCode(condition.releaseCode)
            )
            .groupBy(releaseInfo)
            .fetch()
    }

    override fun findAllByReleaseStatuses(
        condition: ReleaseStatusSearchCondition,
        page: Pageable
    ): Page<ReleaseInfo> {
        return applyPagination(page) {
            it.selectFrom(releaseInfo)
                .where(
                    inReleaseStatus(condition.releaseStatuses),
                    eqPartnerId(condition.partnerId),
                    eqDeliveryAgencyId(condition.deliveryAgencyId),
                    eqOrderCode(condition.orderCode),
                    eqReleaseCode(condition.releaseCode)
                )
                .groupBy(releaseInfo)
        }
    }

    override fun findAllByTrackingNumberStatus(
        trackingNumberStatus: TrackingNumberStatus,
        checkTrackingNumber: Boolean,
        page: Pageable
    ): Page<ReleaseInfo> {
        return applyPagination(page) {
            it.selectFrom(releaseInfo)
                .where(
                    isNotNullTrackingNumber(checkTrackingNumber),
                    eqTrackingNumberStatus(trackingNumberStatus),
                )
                .groupBy(releaseInfo)
        }
    }

    private fun eqReturnStatus(returnStatus: ReturnStatus?) =
        returnStatus?.let { claim.returnStatus.eq(it) }

    private fun eqExchangeStatus(exchangeStatus: ExchangeStatus?) =
        exchangeStatus?.let { claim.exchangeStatus.eq(it) }

    private fun containsOrderNumber(orderNumber: String?) =
        orderNumber?.let { collectedOrder.orderNumber.contains(it) }

    private fun containsStoreName(storeName: String?) =
        storeName?.let { collectedOrder.storeName.contains(it) }

    private fun eqReceiverName(receiverName: String?) =
        receiverName?.let { collectedOrder.receiver.name.eq(it) }

    private fun eqUploadType(uploadType: OrderUploadType?) =
        uploadType?.let { collectedOrder.uploadType.eq(it) }

    private fun eqPartnerId(partnerId: Long?) =
        partnerId?.let { releaseInfo.partnerId.eq(partnerId) }

    private fun eqReleaseStatus(releaseStatus: ReleaseStatus?) =
        releaseStatus?.let { releaseInfo.releaseStatus.eq(it) }

    private fun inReleaseStatus(statuses: Collection<ReleaseStatus>) =
        releaseInfo.releaseStatus.`in`(statuses)

    private fun eqDeliveryAgencyId(deliveryAgencyId: Long?) =
        deliveryAgencyId?.let { releaseInfo.deliveryAgencyId.eq(it) }

    private fun eqOrderCode(orderCode: String?) =
        orderCode?.let { releaseInfo.orderCode.eq(it) }

    private fun containsOrderCode(orderCode: String?) =
        orderCode?.let { releaseInfo.orderCode.contains(it) }

    private fun eqReleaseCode(releaseCode: String?) =
        releaseCode?.let { releaseInfo.releaseCode.eq(it) }

    private fun containsTrackingNumber(trackingNumber: String?) =
        trackingNumber?.let { releaseInfo.trackingNumber.contains(it) }

    private fun isNotNullTrackingNumber(checkTrackingNumber: Boolean) =
        if (checkTrackingNumber) releaseInfo.trackingNumber.isNotNull else null

    private fun eqTrackingNumberStatus(trackingNumberStatus: TrackingNumberStatus?) =
        trackingNumberStatus?.let { releaseInfo.trackingNumberStatus.eq(it) }
}
