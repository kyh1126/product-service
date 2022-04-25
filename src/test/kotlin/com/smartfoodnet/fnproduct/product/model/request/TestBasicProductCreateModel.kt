package com.smartfoodnet.fnproduct.product.model.request

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import io.swagger.annotations.ApiModelProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull

class TestBasicProductCreateModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @field:NotNull
    @ApiModelProperty(
        value = "구분 (BASIC:기본상품/CUSTOM_SUB:고객전용부자재/SUB:공통부자재/PACKAGE:모음상품)",
        allowableValues = "BASIC,CUSTOM_SUB,SUB,PACKAGE", dataType = "Enum", example = "BASIC"
    )
    val type: BasicProductType = BasicProductType.BASIC,

    @ApiModelProperty(value = "화주(고객사) ID", example = "1")
    val partnerId: Long? = null,

    @ApiModelProperty(value = "화주(고객사) 코드", example = "0001")
    val partnerCode: String? = null,

    @ApiModelProperty(value = "출고상품 ID (nosnos)")
    val shippingProductId: Long? = null,

    @ApiModelProperty(value = "상품명")
    val name: String? = null,

    @ApiModelProperty(value = "상품코드")
    val code: String? = null,

    @ApiModelProperty(value = "상품바코드기재여부", allowableValues = "Y,N")
    val barcodeYn: String = "N",

    @ApiModelProperty(value = "상품바코드")
    val barcode: String? = null,

    @ApiModelProperty(value = "상품카테고리 ID")
    val basicProductCategoryId: Long? = null,

    @ApiModelProperty(value = "부자재카테고리 ID")
    val subsidiaryMaterialCategoryId: Long? = null,

    @ApiModelProperty(
        value = "취급온도 (ROOM:상온/REFRIGERATE:냉장/FREEZE:냉동)",
        allowableValues = "ROOM,REFRIGERATE,FREEZE"
    )
    val handlingTemperature: HandlingTemperatureType? = null,

    @ApiModelProperty(value = "입고처 ID")
    val warehouseId: Long? = null,

    @ApiModelProperty(value = "공급가")
    val supplyPrice: Int? = null,

    @ApiModelProperty(value = "유통기한관리여부 (default: N)", allowableValues = "Y,N")
    val expirationDateManagementYn: String = "N",

    @JsonUnwrapped
    var expirationDateInfoModel: ExpirationDateInfoCreateModel? = null,

    @ApiModelProperty(value = "박스입수")
    val piecesPerBox: Int? = null,

    @ApiModelProperty(value = "파레트입수")
    val piecesPerPalette: Int? = null,

    @ApiModelProperty(value = "상품이미지 URL")
    val imageUrl: String? = null,

    @ApiModelProperty(value = "활성화여부 (default: Y)", allowableValues = "Y,N")
    val activeYn: String = "Y",
) {
    @NotNull
    @Valid
    @JsonUnwrapped
    lateinit var singleDimensionCreateModel: SingleDimensionCreateModel

    @NotNull
    @Valid
    @JsonUnwrapped
    lateinit var boxDimensionCreateModel: BoxDimensionCreateModel

    fun toModel(): BasicProductCreateModel {
        return BasicProductCreateModel().also {
            it.id = id
            it.type = type
            it.partnerId = partnerId
            it.partnerCode = partnerCode
            it.name = name
            it.code = code
            it.barcodeYn = barcodeYn
            it.barcode = barcode
            it.basicProductCategoryId = basicProductCategoryId
            it.subsidiaryMaterialCategoryId = subsidiaryMaterialCategoryId
            it.handlingTemperature = handlingTemperature
            it.warehouseId = warehouseId
            it.supplyPrice = supplyPrice
            it.expirationDateManagementYn = expirationDateManagementYn
            it.piecesPerBox = piecesPerBox
            it.piecesPerPalette = piecesPerPalette
            it.imageUrl = imageUrl
            it.activeYn = activeYn
            it.expirationDateInfoModel = expirationDateInfoModel
            it.singleDimensionCreateModel = SingleDimensionCreateModel(0, 0, 0)
            it.boxDimensionCreateModel = BoxDimensionCreateModel(0, 0, 0)
        }
    }
}
