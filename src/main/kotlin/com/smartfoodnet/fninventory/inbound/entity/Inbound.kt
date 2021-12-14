package com.smartfoodnet.fninventory.inbound.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fninventory.inbound.model.vo.InboundMethodType
import com.smartfoodnet.fninventory.inbound.model.vo.InboundStatusType
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import java.time.LocalDateTime
import javax.persistence.*

// 입고 Entity
// 입고등록 -> NOSNOS API SEND -> 입고등록번호 발급
// 입고등록번호를 기준으로 NOSNOS API 요청 -> 입고된 내역을 가져온다
// 1.입고등록번호 -> Inbound 찾아서 -> 하위에 넣어주던가(각각의 inboundDetail을 만들어서 save)
// >>> inboundRepository.findByResistationNo(no) -> inbound 찾고-> inboundDetail 객체를 각각 생성해서 inboundDetail 객체내에서 inbound를 set 해준다 -> inboundDetail save
// 2.입고등록번호 -> Inbound 찾아서 -> cascade 넣어주던가(inbound 양방향)
// >>> findByResistationNo(no) -> inbound 찾고-> inboundDetail 객체를 각각 생성해서 inbound 객체에서 inboundDetail을 add 해주고 -> inbound save() -> inboundDetail save()

// 백엔드 -> 프론트엔드
// 양방향으로 되어있으면 프론트에서 한번 호출로 다 가져갈수있다
// inbound -> inboundDetail mappedBy가 되어있기 때문에 참조가 가능해요
// 단방향이면 inboundDetailRepository가 필요함

@Entity
@Table(name = "inbound")
class Inbound(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inbound_id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    var partnerId: Long? = null,

    var expectedDate: LocalDateTime? = null,

    var registrationNo: String? = null,

    @Enumerated(EnumType.STRING)
    var status: InboundStatusType? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_id", columnDefinition = "BIGINT UNSIGNED")
    var basicProduct: BasicProduct? = null,

    var requestQuantity: Long? = null,

    var actualQuantity: Long? = null,

    @Enumerated(EnumType.STRING)
    var method: InboundMethodType? = null,

    var deliveryName: String? = null,

    var trackingNo: String? = null

): BaseEntity() {
    //
}