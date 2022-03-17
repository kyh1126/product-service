package com.smartfoodnet.fnproduct.order.entity

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import javax.persistence.*

@Entity
class ConfirmRequestOrder(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="confirm_request_id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirm_order_id", columnDefinition = "BIGINT UNSIGNED")
    val confirmOrder : ConfirmOrder,

    @OneToOne
    @JoinColumn(name = "collected_order_id", columnDefinition = "BIGINT UNSIGNED")
    val collectedOrder : CollectedOrder
)