package com.smartfoodnet.fnproduct.product.model.response

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import io.swagger.annotations.ApiModelProperty

data class BasicProductModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "화주(고객사) ID")
    val partnerId: Long,

    @ApiModelProperty(value = "상품명")
    var name: String,

    @ApiModelProperty(value = "상품바코드여부")
    var barcodeYn: String,

    @ApiModelProperty(value = "상품바코드")
    var barcode: String? = null,

    @ApiModelProperty(value = "상품카테고리")
    var category: BasicProductCategoryModel,

    @ApiModelProperty(value = "취급온도")
    var handlingTemperature: Int? = null,

    @ApiModelProperty(value = "단수포장여부")
    var singlePackagingYn: String,

    @ApiModelProperty(value = "입고처")
    var warehouse: WarehouseModel? = null,

    @ApiModelProperty(value = "공급사")
    var supplier: SupplierModel? = null,

    @ApiModelProperty(value = "공급가")
    var supplyPrice: Int? = null,

    @ApiModelProperty(value = "박스입수")
    var piecesPerBox: Int? = null,

    @ApiModelProperty(value = "파레트입수")
    var boxesPerPalette: Int? = null,

    @ApiModelProperty(value = "상품이미지 URL")
    var imageUrl: String? = null,

    @ApiModelProperty(value = "유통기한관리여부 (default: N)", allowableValues = "Y,N")
    var expirationDateManagementYn: String,

    @ApiModelProperty(value = "활성화여부 (default: N)", allowableValues = "Y,N")
    val activeYn: String,
) {

    companion object {
        fun fromEntity(basicProduct: BasicProduct): BasicProductModel {
            return basicProduct.run {
                BasicProductModel(
                    id = id,
                    partnerId = partnerId,
                    name = name,
                    barcodeYn = barcodeYn,
                    barcode = barcode,
                    category = BasicProductCategoryModel.fromEntity(category),
                    handlingTemperature = handlingTemperature,
                    singlePackagingYn = singlePackagingYn,
                    warehouse = warehouse?.let { WarehouseModel.fromEntity(it) },
                    supplier = supplier?.let { SupplierModel.fromEntity(it) },
                    supplyPrice = supplyPrice,
                    piecesPerBox = piecesPerBox,
                    boxesPerPalette = boxesPerPalette,
                    imageUrl = imageUrl,
                    expirationDateManagementYn = expirationDateManagementYn,
                    activeYn = activeYn
                )
            }
        }
    }
}
