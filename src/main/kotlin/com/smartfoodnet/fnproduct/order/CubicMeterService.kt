package com.smartfoodnet.fnproduct.order

import com.smartfoodnet.fnproduct.order.entity.CubicMeter
import com.smartfoodnet.fnproduct.order.support.CubicMeterRepository
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import org.springframework.stereotype.Service

@Service
class CubicMeterService(
    private val cubicMeterRepository: CubicMeterRepository
) {

    fun getByCBM(cbm: Long, temperatureType : HandlingTemperatureType): CubicMeter? {
        return cubicMeterRepository.findByCBM(cbm, temperatureType)
    }

    fun getById(id: Long): CubicMeter? {
        return cubicMeterRepository.findById(id).get()
    }

}