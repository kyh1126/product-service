package com.smartfoodnet.fninventory.inbound.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fninventory.inbound.model.vo.InboundStatusType
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "inbound")
class Inbound(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inbound_id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    val partnerId: Long? = null,

    val expectedDate: LocalDateTime? = null,

    val registrationNo: String? = null,
    val registrationId: Long?= null,

    @Lob
    val memo: String? = null,

    @Enumerated(EnumType.STRING)
    val status: InboundStatusType = InboundStatusType.EXPECTED,

    @OneToMany(mappedBy = "inbound", cascade = [CascadeType.PERSIST])
    val expectedList : MutableList<InboundExpectedDetail> = mutableListOf()

): BaseEntity(){
    fun addExptecdItem(item: InboundExpectedDetail){
        expectedList.add(item)
        item.inbound = this
    }
}