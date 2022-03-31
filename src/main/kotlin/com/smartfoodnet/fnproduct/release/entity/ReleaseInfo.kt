package com.smartfoodnet.fnproduct.release.entity

import com.smartfoodnet.apiclient.response.NosnosReleaseModel
import com.smartfoodnet.common.entity.SimpleBaseEntity
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.order.vo.OrderUploadType
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
        shippingCode = request.shippingCode
        if (shippingCode != null) {
            shippingCodeStatus = shippingCodeStatus
                ?: ShippingCodeStatus.getInitialStatus(uploadType)
            shippingCodeCreatedAt = shippingCodeCreatedAt ?: LocalDateTime.now()
        }

        // 양방향
        releaseProducts.clear()
        releaseProductRequests.forEach { addReleaseProducts(it) }
    }

    fun updateReleaseId(request: NosnosReleaseModel) {
        releaseId = request.releaseId?.toLong()
        releaseCode = request.releaseCode
    }
}
