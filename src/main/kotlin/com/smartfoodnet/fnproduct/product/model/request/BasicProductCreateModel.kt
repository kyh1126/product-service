package com.smartfoodnet.fnproduct.product.model.request

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.BasicProductCategory
import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory
import com.smartfoodnet.fnproduct.product.entity.Warehouse
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import com.smartfoodnet.fnproduct.warehouse.entity.InWarehouse
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotNull

data class BasicProductCreateModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @NotNull
    @ApiModelProperty(
        value = "구분 (BASIC:기본상품/CUSTOM_SUB:고객전용부자재/SUB:공통부자재/PACKAGE:모음상품)",
        allowableValues = "BASIC,CUSTOM_SUB,SUB,PACKAGE", dataType = "Enum", example = "BASIC"
    )
    val type: BasicProductType = BasicProductType.BASIC,

    @ApiModelProperty(value = "화주(고객사) ID", example = "1")
    val partnerId: Long? = null,

    @ApiModelProperty(value = "상품명")
    val name: String? = null,

    @ApiModelProperty(value = "상품코드")
    val code: String? = null,

    @ApiModelProperty(value = "상품바코드기재여부", allowableValues = "Y,N")
    val barcodeYn: String = "N",

    @ApiModelProperty(value = "상품바코드")
    val barcode: String? = null,

    @ApiModelProperty(value = "상품카테고리명", example = "축산 / 가공식품")
    val basicProductCategoryName: String? = null,

    @ApiModelProperty(value = "부자재카테고리")
    val subsidiaryMaterialCategory: SubsidiaryMaterialCategoryCreateModel? = null,

    @ApiModelProperty(
        value = "취급온도 (ROOM:상온/REFRIGERATE:냉장/FREEZE:냉동)",
        allowableValues = "ROOM,REFRIGERATE,FREEZE"
    )
    val handlingTemperature: HandlingTemperatureType? = null,

    @NotNull
    @ApiModelProperty(value = "입고처명", example = "CJ")
    val warehouseName: String,

    @ApiModelProperty(value = "공급가")
    val supplyPrice: Int? = null,

    @ApiModelProperty(value = "단수(포장)여부", allowableValues = "Y,N")
    val singlePackagingYn: String = "N",

    @ApiModelProperty(value = "유통기한관리여부 (default: N)", allowableValues = "Y,N")
    val expirationDateManagementYn: String = "N",

    @ApiModelProperty(value = "박스입수")
    val piecesPerBox: Int? = null,

    @ApiModelProperty(value = "파레트입수")
    val boxesPerPalette: Int? = null,

    @ApiModelProperty(value = "상품이미지 URL")
    val imageUrl: String? = null,

    @ApiModelProperty(value = "활성화여부 (default: N)", allowableValues = "Y,N")
    val activeYn: String = "N",
) {
    @JsonUnwrapped
    var expirationDateInfoModel: ExpirationDateInfoCreateModel? = null

    fun toEntity(
        code: String?,
        basicProductCategory: BasicProductCategory?,
        subsidiaryMaterialCategory: SubsidiaryMaterialCategory?,
        inWarehouse: InWarehouse,
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
            warehouse = inWarehouse,
            supplyPrice = supplyPrice,
            singlePackagingYn = singlePackagingYn,
            expirationDateManagementYn = expirationDateManagementYn,
            expirationDateInfo = expirationDateInfoModel?.toEntity(),
            piecesPerBox = piecesPerBox,
            boxesPerPalette = boxesPerPalette,
            imageUrl = imageUrl,
            activeYn = activeYn
        )
    }
}
