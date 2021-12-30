package com.smartfoodnet.fninventory.inbound.entity

import com.smartfoodnet.common.entity.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "inbound_actual_detail")
class InboundActualDetail(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actual_detail_id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expected_detail_id", columnDefinition = "BIGINT UNSIGNED")
    var inboundExpectedDetail: InboundExpectedDetail,

    val actualQuantity: Long? = null,

    val boxQuantity: Long? = null,

    val palletQuantity: Long? = null,

    val manufactureDate: LocalDateTime? = null,

    val expirationDate: LocalDateTime? = null,

    val actualInboundDate: LocalDateTime? = null,

    ) : BaseEntity() {
}