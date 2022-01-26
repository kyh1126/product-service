package com.smartfoodnet.fninventory.inbound.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fninventory.inbound.model.vo.InboundMethodType
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import java.time.LocalDateTime
import javax.persistence.*

// 등록된 입고 정보 상세 Entity
@Entity
@Table(name = "inbound_expected_detail")
class InboundExpectedDetail(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expected_detail_id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbound_id")
    var inbound: Inbound? = null,

    @OneToMany(mappedBy = "inboundExpectedDetail", cascade = [CascadeType.PERSIST])
    val inboundActualDetail: MutableList<InboundActualDetail>? = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_id", columnDefinition = "BIGINT UNSIGNED")
    var basicProduct: BasicProduct,

    val requestQuantity: Long = 0,

    @Enumerated(EnumType.STRING)
    val method: InboundMethodType? = null,

    val deliveryFlag: Boolean = false,

    val deliveryName: String? = null,

    val trackingNo: String? = null

    ) : BaseEntity() {
}