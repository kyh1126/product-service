package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.fnproduct.order.entity.CubicMeter
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CubicMeterRepository : JpaRepository<CubicMeter, Long>{
    @Query("select c from CubicMeter c where c.above < :cbm and c.below > :cbm and c.temperature = :temperatureType")
    fun findByCBM(cbm: Long, temperatureType: HandlingTemperatureType) : CubicMeter?
}