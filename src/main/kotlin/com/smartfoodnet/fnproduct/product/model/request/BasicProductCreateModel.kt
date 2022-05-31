package com.smartfoodnet.fnproduct.product.model.request

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.product.entity.*
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import com.smartfoodnet.fnproduct.warehouse.entity.InWarehouse
import io.swagger.annotations.ApiModelProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull

class BasicProductCreateModel {
    @ApiModelProperty(value = "id")
    var id: Long? = null

    @NotNull
    @ApiModelProperty(
        value = "구분 (BASIC:기본상품/CUSTOM_SUB:고객전용부자재/SUB:공통부자재/PACKAGE:모음상품)",
        allowableValues = "BASIC,CUSTOM_SUB,SUB,PACKAGE", dataType = "Enum", example = "BASIC"
    )
    var type: BasicProductType = BasicProductType.BASIC

    @ApiModelProperty(value = "화주(고객사) ID", example = "1")
    var partnerId: Long? = null

    @ApiModelProperty(value = "화주(고객사) 코드", example = "0001")
    var partnerCode: String? = null

    @ApiModelProperty(value = "출고상품 ID (nosnos)")
    var shippingProductId: Long? = null

    @ApiModelProperty(value = "상품명")
    lateinit var name: String

    @ApiModelProperty(value = "상품코드")
    var code: String? = null

    @ApiModelProperty(value = "상품바코드기재여부", allowableValues = "Y,N")
    var barcodeYn: String = "N"

    @ApiModelProperty(value = "상품바코드")
    var barcode: String? = null

    @ApiModelProperty(value = "상품카테고리 ID")
    var basicProductCategoryId: Long? = null

    @ApiModelProperty(value = "부자재카테고리 ID")
    var subsidiaryMaterialCategoryId: Long? = null

    @ApiModelProperty(
        value = "취급온도 (ROOM:상온/REFRIGERATE:냉장/FREEZE:냉동)",
        allowableValues = "ROOM,REFRIGERATE,FREEZE"
    )
    var handlingTemperature: HandlingTemperatureType? = null

    @ApiModelProperty(value = "입고처 ID")
    var warehouseId: Long? = null

    @ApiModelProperty(value = "공급가")
    var supplyPrice: Int? = null

    @ApiModelProperty(value = "유통기한관리여부 (default: N)", allowableValues = "Y,N")
    var expirationDateManagementYn: String = "N"

    @JsonUnwrapped
    var expirationDateInfoModel: ExpirationDateInfoCreateModel? = null

    @NotNull
    @Valid
    @JsonUnwrapped
    lateinit var singleDimensionCreateModel: SingleDimensionCreateModel

    @NotNull
    @Valid
    @JsonUnwrapped
    lateinit var boxDimensionCreateModel: BoxDimensionCreateModel

    @ApiModelProperty(value = "박스입수")
    var piecesPerBox: Int? = null

    @ApiModelProperty(value = "파레트입수")
    var piecesPerPalette: Int? = null

    @ApiModelProperty(value = "상품이미지 URL")
    var imageUrl: String? = null

    @ApiModelProperty(value = "활성화여부 (default: Y)", allowableValues = "Y,N")
    var activeYn: String = "Y"

    fun toEntity(
        code: String?,
        basicProductCategory: BasicProductCategory?,
        subsidiaryMaterialCategory: SubsidiaryMaterialCategory?,
        inWarehouse: InWarehouse?,
    ): BasicProduct {
        return BasicProduct(
            partnerId = partnerId,
            type = type,
            barcodeYn = barcodeYn,
            code = code,
            barcode = barcode,
            basicProductCategory = basicProductCategory,
            subsidiaryMaterialCategory = subsidiaryMaterialCategory,
            handlingTemperature = handlingTemperature,
            warehouse = inWarehouse,
            supplyPrice = supplyPrice,
            expirationDateInfo = expirationDateInfoModel?.toEntity(),
            singleDimension = getSingleDimension(),
            boxDimension = getBoxDimension(),
            piecesPerBox = piecesPerBox,
            piecesPerPalette = piecesPerPalette,
            imageUrl = imageUrl,
            activeYn = activeYn
        ).also {
            it.name = name
            it.expirationDateManagementYn = expirationDateManagementYn
        }
    }

    private fun getSingleDimension() =
        if (type == BasicProductType.CUSTOM_SUB) SingleDimension.default
        else singleDimensionCreateModel.toEntity()

    private fun getBoxDimension() =
        if (type == BasicProductType.CUSTOM_SUB) BoxDimension.default
        else boxDimensionCreateModel.toEntity()
}
