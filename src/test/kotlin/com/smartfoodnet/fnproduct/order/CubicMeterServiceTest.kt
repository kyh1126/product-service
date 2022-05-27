package com.smartfoodnet.fnproduct.order

import com.smartfoodnet.base.AbstractTest
import com.smartfoodnet.fnproduct.order.entity.CubicMeter
import com.smartfoodnet.fnproduct.order.support.CubicMeterRepository
import com.smartfoodnet.fnproduct.order.vo.BoxType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired

internal class CubicMeterServiceTest : AbstractTest() {

    @Autowired
    private lateinit var boxService: BoxService

    @Autowired
    private lateinit var cubicMeterRepository: CubicMeterRepository

    @BeforeEach
    internal fun setUp() {
        cubicMeterRepository.save(CubicMeter(0, 5_386_500, HandlingTemperatureType.ROOM, BoxType.ROOM1))
        cubicMeterRepository.save(CubicMeter(5_386_500, 9_725_625, HandlingTemperatureType.ROOM, BoxType.ROOM2))
        cubicMeterRepository.save(CubicMeter(9_725_625, 21_546_000, HandlingTemperatureType.ROOM, BoxType.ROOM4))
        cubicMeterRepository.save(CubicMeter(21_546_000, 31_780_350, HandlingTemperatureType.ROOM, BoxType.ROOM5))
        cubicMeterRepository.save(CubicMeter(31_780_350, 43_605_000, HandlingTemperatureType.ROOM, BoxType.ROOM6))
        cubicMeterRepository.save(CubicMeter(0, 8_128_890, HandlingTemperatureType.REFRIGERATE, BoxType.LOW1))
        cubicMeterRepository.save(CubicMeter(8_128_890, 10_258_920, HandlingTemperatureType.REFRIGERATE, BoxType.LOW2))
        cubicMeterRepository.save(CubicMeter(10_258_920, 14_256_000, HandlingTemperatureType.REFRIGERATE, BoxType.LOW3))
        cubicMeterRepository.save(CubicMeter(14_256_000, 32_364_000, HandlingTemperatureType.REFRIGERATE, BoxType.LOW4))
        cubicMeterRepository.save(CubicMeter(32_364_000, 52_447_500, HandlingTemperatureType.REFRIGERATE, BoxType.LOW5))
    }

    @Test
    @DisplayName("CBM에 따른 박스추천")
    fun getCBM() {
        val cbm1 = CubicMeterValues(300, 200, 150)
        val cubicMeter = boxService.getByCBM(cbm1.cbm, HandlingTemperatureType.ROOM)
        assertEquals(BoxType.ROOM2, cubicMeter?.box)
    }

    @Test
    @DisplayName("추천박스 없음")
    fun getCBM_noResult() {
        val cbm1 = CubicMeterValues(3000, 2000, 1500)
        val cubicMeter = boxService.getByCBM(cbm1.cbm, HandlingTemperatureType.ROOM)
        assertEquals(null, cubicMeter?.box)
    }

    private class CubicMeterValues(
        width : Long,
        length : Long,
        height : Long
    ){
        val cbm : Long = width * length * height
    }

}