package com.smartfoodnet.apiclient.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.smartfoodnet.common.convertYnToInt
import com.smartfoodnet.fnproduct.product.entity.BasicProduct

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PreSalesProductModel(
    val memberId: Long,
    val salesProductCode: String,
    val categoryId: Long? = null,
    val productName: String? = null,
    val manageCode1: String? = null,
    val manageCode2: String? = null,
    val manageCode3: String? = null,
    val productDesc: String? = null,
    val status: Int? = null,
    val useDisplayPeriod: Int? = null,
    val startDt: String? = null,
    val endDt: String? = null
) {
    companion object {
        fun fromEntity(basicProduct: BasicProduct): PreSalesProductModel {
            return basicProduct.run {
                PreSalesProductModel(
                    memberId = partnerId!!,
                    salesProductCode = salesProductCode!!,
                    manageCode3 = handlingTemperature?.desc,
                    productName = name!!,
                    status = convertYnToInt(activeYn),
                    useDisplayPeriod = 0, // 유효기간 사용 여부
                )
            }
        }
    }
}
