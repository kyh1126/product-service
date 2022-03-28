package com.smartfoodnet.fnproduct.release.entity

import com.smartfoodnet.common.entity.SimpleBaseEntity
import com.smartfoodnet.fnproduct.release.model.vo.ReleaseStatus
import com.smartfoodnet.fnproduct.release.model.vo.ShippingCodeStatus
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

    @Column(name = "shipping_code")
    var shippingCode: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_code_status")
    var shippingCodeStatus: ShippingCodeStatus? = null,

    @Column(name = "shipping_code_created_at")
    var shippingCodeCreatedAt: LocalDateTime? = null,

    @Column(name = "delivery_completed_at")
    var deliveryCompletedAt: LocalDateTime? = null,

    @OneToMany(mappedBy = "releaseInfo", cascade = [CascadeType.PERSIST])
    var releaseOrderMappings: MutableSet<ReleaseOrderMapping> = LinkedHashSet()
) : SimpleBaseEntity()
