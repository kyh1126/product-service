package com.smartfoodnet.fnproduct.order.entity

import com.smartfoodnet.fnproduct.order.vo.BoxType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import javax.persistence.*

@Entity
class CubicMeter(
    val above : Long,
    val below : Long,
    @Enumerated(EnumType.STRING)
    val temperature : HandlingTemperatureType,
    @Enumerated(EnumType.STRING)
    val box : BoxType
){
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null
}