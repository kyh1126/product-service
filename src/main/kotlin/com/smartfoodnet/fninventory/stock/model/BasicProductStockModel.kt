package com.smartfoodnet.fninventory.stock.model

import com.smartfoodnet.apiclient.response.NosnosStockModel
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import io.swagger.annotations.ApiModelProperty

data class BasicProductStockModel(
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

    @ApiModelProperty(value = "유통기한 관리 여부")
    val expirationDateManagementYn: String? = null,

    @ApiModelProperty(value = "총재고")
    var totalStockCount: Int? = null,

    @ApiModelProperty(value = "가용재고")
    var normalStockCount: Int? = null,

    @ApiModelProperty(value = "점유 PLT 수")
    var occupiedPLTCount: Int? = null,
) {
    companion object {
        fun fromBasicProduct(basicProduct: BasicProduct): BasicProductStockModel {
            return basicProduct.run {
                BasicProductStockModel(
                    basicProductId = id,
                    basicProductName = name,
                    basicProductCode = code,
                    shippingProductId = shippingProductId,
                    barcode = barcode,
                    expirationDateManagementYn = expirationDateManagementYn
                )
            }
        }
    }

    //TODO wms에서 모델 변경시 수정 필요
    fun fillInNosnosStockValues(nosnosStockModel: NosnosStockModel) {
        totalStockCount = nosnosStockModel.normalStock //TODO 수정필요
        normalStockCount = nosnosStockModel.normalStock
        //occupiedPLTCount
    }
}
