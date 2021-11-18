package com.smartfoodnet.fnproduct.product.model.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.smartfoodnet.common.Constants
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

data class BasicProductModel(
    @ApiModelProperty(value = "id")
    val id: Long? = null,

    @ApiModelProperty(
        value = "구분 (BASIC:기본상품/CUSTOM_SUB:고객전용부자재/SUB:공통부자재/PACKAGE:모음상품)",
        allowableValues = "BASIC,CUSTOM_SUB,SUB,PACKAGE"
    )
    var type: BasicProductType,

    @ApiModelProperty(value = "화주(고객사) ID")
    val partnerId: Long?,

    @ApiModelProperty(value = "상품명")
    var name: String?,

    @ApiModelProperty(value = "상품코드")
    var code: String? = null,

    @ApiModelProperty(value = "상품바코드기재여부", allowableValues = "Y,N")
    var barcodeYn: String,

    @ApiModelProperty(value = "상품바코드")
    var barcode: String? = null,

    @ApiModelProperty(value = "상품카테고리명")
    var basicProductCategoryName: String? = null,

    @ApiModelProperty(value = "부자재카테고리")
    var subsidiaryMaterialCategory: SubsidiaryMaterialCategoryModel?,

    @ApiModelProperty(
        value = "취급온도 (ROOM:상온/REFRIGERATE:냉장/FREEZE:냉동)",
        allowableValues = "ROOM,REFRIGERATE,FREEZE"
    )
    var handlingTemperature: HandlingTemperatureType? = null,

    @ApiModelProperty(value = "입고처명")
    var warehouseName: String? = null,

    @ApiModelProperty(value = "공급가")
    var supplyPrice: Int? = null,

    @ApiModelProperty(value = "단수(포장)여부", allowableValues = "Y,N")
    var singlePackagingYn: String,

    @ApiModelProperty(value = "유통기한관리여부 (default: N)", allowableValues = "Y,N")
    var expirationDateManagementYn: String,

    @JsonUnwrapped
    @ApiModelProperty(value = "유통기한정보")
    var expirationDateInfoModel: ExpirationDateInfoModel? = null,

    @ApiModelProperty(value = "박스입수")
    var piecesPerBox: Int? = null,

    @ApiModelProperty(value = "파레트입수")
    var boxesPerPalette: Int? = null,

    @ApiModelProperty(value = "상품이미지 URL")
    var imageUrl: String? = null,

    @ApiModelProperty(value = "활성화여부 (default: N)", allowableValues = "Y,N")
    val activeYn: String,

    @ApiModelProperty(value = "생성시간")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val createdAt: LocalDateTime? = null,

    @ApiModelProperty(value = "업데이트시간")
    @JsonFormat(pattern = Constants.TIMESTAMP_FORMAT)
    val updatedAt: LocalDateTime? = null,
) {

    companion object {
        fun fromEntity(basicProduct: BasicProduct): BasicProductModel {
            return basicProduct.run {
                val level2CategoryName = subsidiaryMaterialCategory?.level2Category?.keyName

                BasicProductModel(
                    id = id,
                    type = type,
                    partnerId = partnerId,
                    name = if (type == BasicProductType.SUB) level2CategoryName else name,
                    code = code,
                    barcodeYn = barcodeYn,
                    barcode = barcode,
                    basicProductCategoryName = basicProductCategory?.let {
                        listOf(
                            it.level1Category.keyName,
                            it.level2Category?.keyName
                        ).joinToString(" / ")
                    },
                    subsidiaryMaterialCategory = subsidiaryMaterialCategory?.let {
                        SubsidiaryMaterialCategoryModel.fromEntity(it)
                    },
                    handlingTemperature = handlingTemperature,
                    warehouseName = warehouse?.name,
                    supplyPrice = supplyPrice,
                    singlePackagingYn = singlePackagingYn,
                    expirationDateManagementYn = expirationDateManagementYn,
                    expirationDateInfoModel = expirationDateInfo?.let {
                        ExpirationDateInfoModel.fromEntity(it)
                    },
                    piecesPerBox = piecesPerBox,
                    boxesPerPalette = boxesPerPalette,
                    imageUrl = imageUrl,
                    activeYn = activeYn,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
            }
        }
    }
}
