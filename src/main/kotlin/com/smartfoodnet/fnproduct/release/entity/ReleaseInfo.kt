package com.smartfoodnet.fnproduct.release.entity

import com.smartfoodnet.apiclient.response.NosnosReleaseModel
import com.smartfoodnet.common.entity.SimpleBaseEntity
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
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

    @OneToMany(mappedBy = "releaseInfo", cascade = [CascadeType.PERSIST])
    var releaseProducts: MutableSet<ReleaseProduct> = LinkedHashSet(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "order_id", referencedColumnName = "orderId",
        insertable = false, updatable = false, // read-only
        foreignKey = ForeignKey(value = ConstraintMode.NO_CONSTRAINT)
    )
    var confirmOrder: ConfirmOrder? = null
) : SimpleBaseEntity() {
    fun addReleaseProducts(releaseProduct: ReleaseProduct) {
        releaseProducts.add(releaseProduct)
        releaseProduct.releaseInfo = this
    }

    fun update(
        request: NosnosReleaseModel,
        releaseProductRequests: Set<ReleaseProduct>,
        uploadType: OrderUploadType
    ) {
        releaseStatus = ReleaseStatus.fromReleaseStatus(request.releaseStatus!!)
        deliveryAgencyId = request.deliveryAgencyId?.toLong()
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
        releaseId = request.releaseId?.toLong()
        releaseCode = request.releaseCode
    }

    fun updateTrackingNumberStatus(status: TrackingNumberStatus) {
        trackingNumberStatus = status
    }

    fun updateDeliveryCompletedAt(procDateTime: LocalDateTime?) {
        deliveryCompletedAt = procDateTime
    }
}
