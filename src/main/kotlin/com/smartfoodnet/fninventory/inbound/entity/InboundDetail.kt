package com.smartfoodnet.fninventory.inbound.entity

import com.smartfoodnet.common.entity.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

// 등록된 입고 정보 상세 Entity
@Entity
@Table(name = "inbound_detail")
class InboundDetail(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inbound_id")
    var inbound: Inbound? = null,

    // 실입고수량
    var actualQuantity: Long? = null,

    // 박스수
    var boxQuantity: Long? = null,

    // 팔레트수
    var palletQuantity: Long? = null,

    // 제조일자
    var manufactureDate: LocalDateTime? = null,

    // 유통기한
    var expirationDate: LocalDateTime? = null,

    // 실입고일자
    var actualInboundDate: LocalDateTime? = null

    ) : BaseEntity() {
}