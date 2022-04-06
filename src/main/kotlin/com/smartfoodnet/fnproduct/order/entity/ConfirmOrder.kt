package com.smartfoodnet.fnproduct.order.entity

import com.smartfoodnet.common.entity.BaseEntity
import org.hibernate.annotations.DynamicUpdate
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@DynamicUpdate
class ConfirmOrder(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "confirm_order_id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,
    val partnerId: Long,
    val bundleNumber: String,
    val shippingMethodType: Int? = null,
    val requestShippingDate: LocalDateTime,
    val shippingMessage: String? = null,
    @Embedded
    val receiver: Receiver,
    @Embedded
    val memo: Memo? = null,
    @OneToMany(mappedBy = "confirmOrder", cascade = [CascadeType.PERSIST], orphanRemoval = true)
    val requestOrderList: MutableList<ConfirmRequestOrder> = mutableListOf()
) : BaseEntity(), Serializable {
    var orderId: Long? = null
    var orderCode: String? = null

    fun addRequestOrder(confirmRequestOrder: ConfirmRequestOrder) {
        requestOrderList.add(confirmRequestOrder)
    }

    fun setOrderInfo(id: Long, code: String) {
        orderId = id
        orderCode = code
    }

    companion object {
        private const val serialVersionUID = -126L
    }
}

/*
    ConfirmOrder(1)
    id, partnerId, bundleNumber, orderId, orderCode
    shippingMethodType, 출고희망일, receiverInfo,
    memo1~5

    ConfirmOrderDetail(N)
    id(PK), confirmOrder(FK), collectedOrder(1:1 ref confirmProduct)
*/
