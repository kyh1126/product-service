package com.smartfoodnet.fnproduct.order.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.claim.model.vo.ClaimStatus
import com.smartfoodnet.fnproduct.order.vo.*
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

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    /**
     * 쇼핑몰 수집후 동기화시 사용되는 필드
     */
    var storeSyncStatus: StoreSyncStatus = StoreSyncStatus.NEW,

    val bundleNumber: String,

    val storeName: String,

    val storeCode: String?,

    val storeId: Long?,

    val userStoreId: String? = null,

    val orderedAt: LocalDateTime? = null,

    val collectedAt: LocalDateTime? = null,

    val statusUpdatedAt: LocalDateTime? = null,

    val orderNumber: String,

    @Enumerated(EnumType.STRING)
    var claimStatus: ClaimStatus = ClaimStatus.UNREGISTERED,

    @Column(name = "delivery_type")
    @Convert(converter = DeliveryTypeConverter::class)
    val deliveryType: DeliveryType = DeliveryType.PARCEL,

    var expectedDeliveryDate: LocalDateTime? = null,

    @JoinColumn(name = "store_product_id", columnDefinition = "BIGINT UNSIGNED")
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    var storeProduct: StoreProduct? = null,

    @OneToMany(mappedBy = "collectedOrder", fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST], orphanRemoval = true)
    val confirmProductList: MutableList<ConfirmProduct> = mutableListOf(),

    @Embedded
    val collectedProductInfo: CollectedProductInfo,

    val price: Double? = null,

    val shippingPrice: Double? = null,

    val quantity: Int,

    @Embedded
    val receiver: Receiver,

    @Embedded
    val sender: Sender? = null,

    @Column(name = "upload_type")
    @Enumerated(EnumType.STRING)
    val uploadType: OrderUploadType = OrderUploadType.API,

    val unprocessed: Boolean = false
) : BaseEntity() {
    val isConnectedStoreProduct
        @JsonIgnore @Transient
        get() = storeProduct?.storeProductMappings?.isNotEmpty() ?: false

    fun nextStep() {
        status = status.next()
    }

    fun addConfirmProduct(confirmProduct: ConfirmProduct) {
        confirmProduct.collectedOrder = this
        confirmProductList.add(confirmProduct)
    }

    fun clearConfirmProduct() {
        confirmProductList.clear()
    }
}
