package com.smartfoodnet.fnproduct.order

import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fnproduct.order.entity.CubicMeter
import com.smartfoodnet.fnproduct.order.support.CubicMeterRepository
import com.smartfoodnet.fnproduct.order.vo.BoxType
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import org.springframework.stereotype.Service

@Service
class BoxService(
    val cubicMeterRepository: CubicMeterRepository
) {

    /**
     * 기본상품 기준으로 추천 박스를 계산하여 가져온다. null인 경우 검토필요로 가져온다
     * @param collectedOrderList 기본상품 리스트
     * @return BoxType
     */
    fun getRecommendBox(basicProductList: List<BasicProduct>): BoxType {
        val totalCbm = sumProductCbm(basicProductList)
        val handleTemperature = getProductHandleTemperature(basicProductList)
        log.info("totalCbm -> {}", totalCbm)
        return getByCBM(totalCbm, handleTemperature)?.box ?: BoxType.CHECK
    }

    private fun sumProductCbm(basicProductList: List<BasicProduct>): Long =
        basicProductList.sumOf(BasicProduct::singleCbm)

    /**
     * 상온, 저온을 반환한다
     * 만약 상온/저온 복합일 경우 저온으로 반환한다
     * @param basicProductList 기본상품 리스트
     * @return HandlingTemperatureType
     */
    private fun getProductHandleTemperature(basicProductList: List<BasicProduct>): HandlingTemperatureType {
        return if (basicProductList.all { b -> b.handlingTemperature == HandlingTemperatureType.ROOM })
            HandlingTemperatureType.ROOM
        else
            HandlingTemperatureType.REFRIGERATE
    }


    fun getByCBM(cbm: Long, temperatureType : HandlingTemperatureType): CubicMeter? {
        return cubicMeterRepository.findByCBM(cbm, temperatureType)
    }

    companion object : Log
}