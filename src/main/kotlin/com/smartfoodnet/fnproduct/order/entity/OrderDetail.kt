package com.smartfoodnet.fnproduct.order.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.order.model.OrderStatus
import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "order_detail")
@Where(clause = "deleted_at is NULL")
class OrderDetail(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @Column(name = "partner_id", columnDefinition = "BIGINT UNSIGNED")
    var partnerId: Long? = null,

    @Column(name = "order_unique_key", unique = true)
    val orderUniqueKey: String? = null,

    @Column(name = "store_name")
    var storeName: String? = null,

    @Column(name = "store_id")
    var storeId: Long? = null,

    @Column(name = "user_store_id")
    var userStoreId: String? = null,

    @Column(name = "ordered_at")
    var orderedAt: LocalDateTime? = null,

    @Column(name = "collected_at")
    val collectedAt: LocalDateTime? = null,

    @Column(name = "status_updated_at")
    val statusUpdatedAt: LocalDateTime? = null,

    @Column(name = "order_number")
    var orderNumber: String? = null,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: OrderStatus? = null,

    @Column(name = "claim_status")
    var claimStatus: String? = null,

    @Column(name = "delivery_type")
    var deliveryType: String? = null,

    @Column(name = "expected_delivery_date")
    var expectedDeliveryDate: LocalDateTime? = null,

    @JoinColumn(name = "store_product_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    var storeProduct: StoreProduct? = null,

    @Column(name = "price")
    var price: Double? = null,

    @Column(name = "shipping_price")
    var shippingPrice: Double? = null,

    @Embedded
    var receiver: Receiver? = null,

    @Embedded
    var sender: Sender? = null,

    @Column(name = "upload_type")
    var uploadType: String? = null

): BaseEntity()