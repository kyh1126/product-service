package com.smartfoodnet.fnproduct.product.model.request

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory
import com.smartfoodnet.fnproduct.product.entity.Warehouse
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import io.swagger.annotations.ApiModelProperty

data class BasicProductCreateModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(value = "구분 (BASIC:기본상품/CUSTOM_SUB:고객전용부자재/SUB:공통부자재/PACKAGE:모음상품)",
        allowableValues = "BASIC,CUSTOM_SUB,SUB,PACKAGE", dataType = "Enum", example = "BASIC")
    val type: BasicProductType = BasicProductType.BASIC,

    @ApiModelProperty(value = "화주(고객사) ID")
    val partnerId: Long? = null,

    @ApiModelProperty(value = "상품명")
    val name: String? = null,

    @ApiModelProperty(value = "상품코드")
    val code: String? = null,

    @ApiModelProperty(value = "상품바코드기재여부")
    val barcodeYn: String = "N",

    @ApiModelProperty(value = "상품바코드")
    val barcode: String? = null,

    @ApiModelProperty(value = "상품카테고리")
    val basicProductCategory: BasicProductCategoryCreateModel? = null,

    @ApiModelProperty(value = "부자재카테고리")
    val subsidiaryMaterialCategory: SubsidiaryMaterialCategoryCreateModel? = null,

    @ApiModelProperty(value = "취급온도 (ROOM:상온/REFRIGERATE:냉장/FREEZE:냉동)", allowableValues = "ROOM,REFRIGERATE,FREEZE")
    val handlingTemperature: HandlingTemperatureType? = null,

    @ApiModelProperty(value = "입고처")
    val warehouse: WarehouseCreateModel? = null,

    @ApiModelProperty(value = "공급가")
    val supplyPrice: Int? = null,

    @ApiModelProperty(value = "단수(포장)여부")
    val singlePackagingYn: String = "N",

    @ApiModelProperty(value = "유통기한관리여부 (default: N)", allowableValues = "Y,N")
    val expirationDateManagementYn: String = "N",

    @ApiModelProperty(value = "유통기한정보")
    val expirationDateInfoModel: ExpirationDateInfoCreateModel? = null,

    @ApiModelProperty(value = "박스입수")
    val piecesPerBox: Int? = null,

    @ApiModelProperty(value = "파레트입수")
    val boxesPerPalette: Int? = null,

    @ApiModelProperty(value = "상품이미지 URL")
    val imageUrl: String? = null,

    @ApiModelProperty(value = "활성화여부 (default: N)", allowableValues = "Y,N")
    val activeYn: String = "N",
) {
    fun toEntity(
        code: String,
        basicProductCategory: BasicProductCategory?,
        subsidiaryMaterialCategory: SubsidiaryMaterialCategory?,
        warehouse: Warehouse,
    ): BasicProduct {
        return BasicProduct(
            type = type,
            partnerId = partnerId,
            name = name,
            code = code,
            barcodeYn = barcodeYn,
            barcode = barcode,
            basicProductCategory = basicProductCategory,
            subsidiaryMaterialCategory = subsidiaryMaterialCategory,
            handlingTemperature = handlingTemperature,
            warehouse = warehouse,
            supplyPrice = supplyPrice,
            singlePackagingYn = singlePackagingYn,
            expirationDateManagementYn = expirationDateManagementYn,
            piecesPerBox = piecesPerBox,
            boxesPerPalette = boxesPerPalette,
            imageUrl = imageUrl,
            activeYn = activeYn
        ).apply {
            expirationDateInfo = expirationDateInfoModel?.toEntity(this)
        }
    }
}
