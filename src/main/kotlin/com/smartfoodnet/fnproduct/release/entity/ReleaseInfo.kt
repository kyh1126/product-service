package com.smartfoodnet.fnproduct.release.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.apiclient.response.GetOutboundCancelModel.CancelledOutboundModel
import com.smartfoodnet.apiclient.response.NosnosReleaseModel
import com.smartfoodnet.common.entity.SimpleBaseEntity
import com.smartfoodnet.fnproduct.claim.entity.Claim
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
import com.smartfoodnet.fnproduct.release.model.vo.PausedBy
import com.smartfoodnet.fnproduct.release.model.vo.ReleaseStatus
import com.smartfoodnet.fnproduct.release.model.vo.TrackingNumberStatus
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "release_info")
class ReleaseInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @Column(name = "partner_id", columnDefinition = "BIGINT UNSIGNED")
    var partnerId: Long,

    @Column(name = "order_id")
    var orderId: Long,

    @Column(name = "order_code")
    var orderCode: String,

    @Column(name = "release_id")
    var releaseId: Long? = null,

    @Column(name = "release_code")
    var releaseCode: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "release_status")
    var releaseStatus: ReleaseStatus = ReleaseStatus.BEFORE_RELEASE_REQUEST,

    @Column(name = "delivery_agency_id")
    var deliveryAgencyId: Long? = null,

    @Column(name = "tracking_number")
    var trackingNumber: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "tracking_number_status")
    var trackingNumberStatus: TrackingNumberStatus = TrackingNumberStatus.NONE,

    @Column(name = "tracking_number_created_at")
    var trackingNumberCreatedAt: LocalDateTime? = null,

    @Column(name = "delivery_completed_at")
    var deliveryCompletedAt: LocalDateTime? = null,

    @Column(name = "paused_at")
    var pausedAt: LocalDateTime? = null,

    @OneToMany(mappedBy = "releaseInfo", cascade = [CascadeType.PERSIST])
    var releaseProducts: MutableSet<ReleaseProduct> = LinkedHashSet(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirm_order_id", columnDefinition = "BIGINT UNSIGNED")
    var confirmOrder: ConfirmOrder? = null,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "releaseInfo")
    @JsonIgnore
    var claim: Claim? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "paused_by")
    var pausedBy: PausedBy? = null,

    @Column(name = "paused_reason")
    var pausedReason: String? = null,

    @Column(name = "previous_order_code")
    var previousOrderCode: String? = null,

    @Column(name = "previous_release_code")
    var previousReleaseCode: String? = null,

    @Column(name = "next_order_code")
    var nextOrderCode: String? = null,
) : SimpleBaseEntity() {
    fun addReleaseProducts(releaseProduct: ReleaseProduct) {
        releaseProducts.add(releaseProduct)
        releaseProduct.releaseInfo = this
    }

    fun update(
        request: NosnosReleaseModel,
        releaseProductRequests: Set<ReleaseProduct>,
        uploadType: OrderUploadType,
        pausedBy: PausedBy = PausedBy.NOSNOS
    ) {
        releaseStatus = ReleaseStatus.fromReleaseStatus(request.releaseStatus!!)
        deliveryAgencyId = request.deliveryAgencyId
        trackingNumber = request.trackingNumber
        if (trackingNumber != null) {
            trackingNumberStatus =
                if (trackingNumberStatus.isInProgress()) trackingNumberStatus
                else TrackingNumberStatus.getInitialStatus(uploadType)
            trackingNumberCreatedAt = trackingNumberCreatedAt ?: LocalDateTime.now()
        }

        // 양방향
        releaseProducts.clear()
        releaseProductRequests.forEach { addReleaseProducts(it) }
    }

    fun updateReleaseId(request: NosnosReleaseModel) {
        releaseId = request.releaseId
        releaseCode = request.releaseCode
    }

    fun updateTrackingNumberStatus(status: TrackingNumberStatus) {
        trackingNumberStatus = status
    }

    fun updateDeliveryCompletedAt(procDateTime: LocalDateTime?) {
        releaseStatus = ReleaseStatus.DELIVERY_COMPLETED
        deliveryCompletedAt = procDateTime
    }

    fun processNextOrderCode(nextOrderCode: String) {
        this.nextOrderCode = nextOrderCode
        cancel()
    }

    fun linkPreviousCodes(previousOrderCode: String? = null, previousReleaseCode: String? = null) {
        this.previousOrderCode = previousOrderCode
        this.previousReleaseCode = previousReleaseCode
    }

    fun processNosnosPause(cancelledOutbound: CancelledOutboundModel?) {
        pause(cancelledOutbound?.pausedAt, PausedBy.NOSNOS)
        pausedReason = cancelledOutbound?.cancelReason
    }

    fun pause(pausedAt: LocalDateTime? = null, pausedBy: PausedBy) {
        this.releaseStatus = ReleaseStatus.RELEASE_PAUSED
        this.pausedAt = pausedAt ?: LocalDateTime.now()
        this.pausedBy = pausedBy
    }

    fun cancel() {
        releaseStatus = ReleaseStatus.RELEASE_CANCELLED
    }
}
