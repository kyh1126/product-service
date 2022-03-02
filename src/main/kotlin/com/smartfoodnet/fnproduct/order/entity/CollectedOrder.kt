package com.smartfoodnet.fnproduct.order.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.order.model.OrderStatus
import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@DynamicUpdate
class CollectedOrder(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collect_order_id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @Column(columnDefinition = "BIGINT UNSIGNED")
    val partnerId: Long? = null,

    @Column(unique = true)
    val orderUniqueKey: String? = null,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.NEW,

    val bundleNumber: String,

    val storeName: String? = null,

    val storeCode: String? = null,

    val storeId: Long? = null,

    val userStoreId: String? = null,

    val orderedAt: LocalDateTime? = null,

    val collectedAt: LocalDateTime? = null,

    val statusUpdatedAt: LocalDateTime? = null,

    val orderNumber: String? = null,

    var claimStatus: String? = null,

    val deliveryType: String? = "택배",

    var expectedDeliveryDate: LocalDateTime? = null,

    @JoinColumn(name = "store_product_id", columnDefinition = "BIGINT UNSIGNED")
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    var storeProduct: StoreProduct? = null,

    @Embedded
    val collectedProductInfo: CollectedProductInfo,

    val price: Double? = null,

    val shippingPrice: Double? = null,

    val quantity: Int? = null,

    @Embedded
    val receiver: Receiver? = null,

    @Embedded
    val sender: Sender? = null,

    val uploadType: String? = "자동"

): BaseEntity()