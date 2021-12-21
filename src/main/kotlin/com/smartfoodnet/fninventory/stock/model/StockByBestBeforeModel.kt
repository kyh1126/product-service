package com.smartfoodnet.fninventory.stock.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.smartfoodnet.apiclient.response.NosnosStockModel
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fninventory.stock.entity.StockByBestBefore
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class StockByBestBeforeModel(
    @ApiModelProperty(value = "기본상품 ID")
    val basicProductId: Long? = null,

    @ApiModelProperty(value = "기본상품명")
    val basicProductName: String? = null,

    @ApiModelProperty(value = "기본상품 코드")
    val basicProductCode: String? = null,

    @ApiModelProperty(value = "출고상품 ID")
    val shippingProductId: Long? = null,

    @ApiModelProperty(value = "상품 바코드")
    val barcode: String? = null,

    @ApiModelProperty(value = "상미기한")
    val bestBefore: Int? = null,

    @ApiModelProperty(value = "제조일자")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val manufactureDate: LocalDateTime? = null,


    @ApiModelProperty(value = "유통기한")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val expirationDate: LocalDateTime? = null,

    @ApiModelProperty(value = "총재고")
    var totalStockCount: Int? = null,

    @ApiModelProperty(value = "가용재고")
    var availableStockCount: Int? = null,

    ) {
    companion object {
        fun fromStockByBestBefore(stockByBestBefore: StockByBestBefore): StockByBestBeforeModel {
            return stockByBestBefore.run {
                StockByBestBeforeModel(
                    basicProductId = basicProduct.id,
                    basicProductName = basicProduct.name,
                    basicProductCode = basicProduct.code,
                    barcode = basicProduct.barcode,
                    shippingProductId = shippingProductId,
                    bestBefore = bestBefore,
                    manufactureDate = manufactureDate,
                    expirationDate = expirationDate,
                    totalStockCount = totalStockCount,
                    availableStockCount = availableStockCount,
                )
            }
        }
    }

    //TODO wms에서 모델 변경시 수정 필요
    fun fillInNosnosStockValues(nosnosStockModel: NosnosStockModel) {
        totalStockCount = nosnosStockModel.normalStock //TODO 수정필요
        availableStockCount = nosnosStockModel.normalStock
        //occupiedPLTCount
    }
}
