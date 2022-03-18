package com.smartfoodnet.fnproduct.order.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.store.entity.StoreProduct
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
    val partnerId: Long,

    @Column(unique = true)
    val orderUniqueKey: String? = null,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.NEW,

    val bundleNumber: String,

    val storeName: String,

    val storeCode: String,

    val storeId: Long,

    val userStoreId: String? = null,

    val orderedAt: LocalDateTime? = null,

    val collectedAt: LocalDateTime? = null,

    val statusUpdatedAt: LocalDateTime? = null,

    val orderNumber: String,

    var claimStatus: String? = null,

    val deliveryType: String? = "택배",

    var expectedDeliveryDate: LocalDateTime? = null,

    @JoinColumn(name = "store_product_id", columnDefinition = "BIGINT UNSIGNED")
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    var storeProduct: StoreProduct? = null,

    @OneToMany(mappedBy = "collectedOrder", fetch = FetchType.LAZY)
    val confirmProductList : List<ConfirmProduct> = listOf(),

    @Embedded
    val collectedProductInfo: CollectedProductInfo,

    val price: Double? = null,

    val shippingPrice: Double? = null,

    val quantity: Int,

    @Embedded
    val receiver: Receiver,

    @Embedded
    val sender: Sender? = null,

    val uploadType: String? = "자동",

    val unprocessed: Boolean = false

): BaseEntity(){
    val isConnectedStoreProduct
        @JsonIgnore @Transient
        get() = storeProduct?.storeProductMappings?.isNotEmpty()?:false

    fun nextStep(){
        status = status.next()
    }
}