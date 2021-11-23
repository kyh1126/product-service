package com.smartfoodnet.fnproduct.order.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "order_detail")
class OrderDetail(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @Column(name = "partner_id", columnDefinition = "BIGINT UNSIGNED")
    var partnerId: Long? = null,

    @ApiModelProperty(value = "중복처리를 위한 쇼핑몰 종속적 유니크 키")
    @Column(name = "order_unique_key", unique = true)
    val orderUniqueKey: String? = null,

    @Column(name = "store_name")
    var storeName: String? = null,

    @Column(name = "store_code")
    var storeCode: String? = null,

    @Column(name = "user_store_id")
    var userStoreId: String? = null,

    @Column(name = "ordered_at")
    var orderedAt: LocalDateTime? = null,

    @Column(name = "order_number")
    var orderNumber: String? = null,

    @Column(name = "status")
    var status: String? = null,

    @Column(name = "claim_status")
    var claimStatus: String? = null,

    @Column(name = "delivery_type")
    var deliveryType: String? = null,

    @Column(name = "desired_delivery_date")
    var desiredDeliveryDate: LocalDateTime? = null,

    @JoinColumn(name = "store_product_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    var storeProduct: StoreProduct? = null,

    @Column(name = "price")
    var price: Double? = null,

    @Column(name = "shipping_price")
    var shippingPrice: String? = null,

    @Embedded
    var receiver: Receiver? = null,

    @Embedded
    var sender: Sender? = null,

    @Column(name = "upload_type")
    var uploadType: String? = null

): BaseEntity()