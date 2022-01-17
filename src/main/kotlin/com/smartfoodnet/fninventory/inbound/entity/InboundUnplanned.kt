package com.smartfoodnet.fninventory.inbound.entity

import com.smartfoodnet.apiclient.response.GetInboundWorkModel
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "inbound_unplanned")
class InboundUnplanned(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,
//    val uniqueId : String,
    val partnerId : Long,
    val workDate : LocalDateTime,
    val workType : Int,
    val receivingType : Int,
    @ManyToOne
    @JoinColumn(name = "basic_product_id")
    val basicProduct : BasicProduct? = null,
    val quantity : Int,
    val makeDate : String? = null,
    val expireDate : String? = null,
    val locationId : Int? = null,
    val boxQuantity : Int? = null,
    val palletQuantity : Int? = null,
    @Lob
    val memo : String? = null,
    val workerId : Long? = null
)
