package com.smartfoodnet.fnproduct.release.entity

import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.release.model.vo.ReleaseStatus
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "release_info")
class ReleaseInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirm_order_id", columnDefinition = "BIGINT UNSIGNED")
    var confirmOrder: ConfirmOrder? = null,

    @Column(name = "order_id")
    var orderId: Long? = null,

    @Column(name = "order_code")
    var orderCode: String? = null,

    @Column(name = "release_id")
    var releaseId: Long? = null,

    @Column(name = "release_code")
    var releaseCode: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "release_status")
    var releaseStatus: ReleaseStatus = ReleaseStatus.RELEASE_REQUESTED,

    @Column(name = "shipping_code")
    var shippingCode: String? = null,

    @Column(name = "delivery_agency_id")
    var deliveryAgencyId: Long? = null,

    @Column(name = "delivery_agency_name")
    var deliveryAgencyName: String? = null,

    @Column(name = "shipping_code_created_at")
    var shippingCodeCreatedAt: LocalDateTime? = null,

    @Column(name = "delivery_completed_at")
    var deliveryCompletedAt: LocalDateTime? = null,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
)
