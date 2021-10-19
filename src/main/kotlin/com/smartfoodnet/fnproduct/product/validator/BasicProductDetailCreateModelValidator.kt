package com.smartfoodnet.fnproduct.product.validator

import com.smartfoodnet.common.error.CreateModelValidator
import com.smartfoodnet.common.error.SaveState
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.product.model.request.BasicProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import org.springframework.stereotype.Component
import org.springframework.validation.Errors

@Component
class BasicProductDetailCreateModelValidator(
    private val basicProductRepository: BasicProductRepository,
    private val basicProductCreateModelValidator: BasicProductCreateModelValidator,
) : CreateModelValidator<BasicProductDetailCreateModel> {
    override fun supports(clazz: Class<*>): Boolean = clazz.isAssignableFrom(BasicProductDetailCreateModel::class.java)

    override fun validate(saveState: SaveState, target: BasicProductDetailCreateModel, errors: Errors) {
        when (target.basicProductModel.type) {
            BasicProductType.BASIC -> checkRequiredFieldsBasicType(saveState, target, errors)
            BasicProductType.CUSTOM_SUB -> checkRequiredFieldsCustomSubType(target, errors)
            else -> Unit
        }

        checkBarcode(saveState, target, errors)

        checkExpirationDate(target, errors)
    }

    private fun checkBarcode(saveState: SaveState, target: BasicProductDetailCreateModel, errors: Errors) {
        if (saveState == SaveState.UPDATE) return

        with(target.basicProductModel) {
            if (barcodeYn.isNotEmpty() && barcodeYn == "Y") {
                validateEmpty(errors, "basicProductModel.barcode", "상품바코드", barcode)
                validateEmpty(errors, "basicProductModel.partnerId", "화주(고객사) ID", partnerId)

                if (barcode == null || partnerId == null) return

                if (barcode.toLongOrNull() == null) {
                    errors.rejectValue("basicProductModel.barcode", "barcode.invalid", "숫자 입력만 가능합니다.")
                }
                basicProductRepository.findByPartnerIdAndBarcode(partnerId, barcode)?.let {
                    errors.rejectValue("basicProductModel.barcode", "barcode.invalid", "사용중인 상품바코드가 있습니다.")
                }
            }
        }
    }

    private fun checkExpirationDate(target: BasicProductDetailCreateModel, errors: Errors) {
        with(target.basicProductModel) {
            if (expirationDateManagementYn.isNotEmpty() && expirationDateManagementYn == "Y") {
                validateEmpty(errors, "basicProductModel.expirationDateInfoModel", "유통기한정보", expirationDateInfoModel)

                if (expirationDateInfoModel == null) return
                with(expirationDateInfoModel) {
                    if (manufactureDateWriteYn == "N" && expirationDateWriteYn == "N") {
                        errors.reject(
                            "basicProductModel.expirationDateInfoModel",
                            "유통기한정보의 유통기한 기재 여부, 제조일자 기재 여부 중 하나는 YES 여야 합니다."
                        )
                    }
                    if (expirationDateWriteYn == "Y") {
                        validateNull(errors,
                            "basicProductModel.expirationDateInfoModel.expirationDate",
                            "유통기한(제조일+X일)",
                            expirationDate)
                    }
                }
            }
        }
    }

    private fun checkRequiredFieldsBasicType(
        saveState: SaveState,
        target: BasicProductDetailCreateModel,
        errors: Errors,
    ) {
        val basicProductModel = target.basicProductModel
        val subsidiaryMaterialModels = target.subsidiaryMaterialModels

        if (basicProductModel.type != BasicProductType.BASIC) return

        with(basicProductModel) {
            validateEmpty(errors, "basicProductModel.partnerId", "화주(고객사) ID", partnerId)
            validateEmpty(errors, "basicProductModel.name", "상품명", name)
            validateEmpty(errors, "basicProductModel.barcodeYn", "상품바코드기재여부", barcodeYn)
            validateEmpty(errors, "basicProductModel.handlingTemperature", "취급온도", handlingTemperature)
            validateEmpty(errors, "basicProductModel.basicProductCategory", "상품카테고리", basicProductCategory)
            validateEmpty(errors, "basicProductModel.singlePackagingYn", "단수(포장)여부", singlePackagingYn)
            validateEmpty(
                errors,
                "basicProductModel.expirationDateManagementYn",
                "유통기한관리여부",
                expirationDateManagementYn
            )
            validateEmpty(errors, "basicProductModel.piecesPerBox", "박스입수", piecesPerBox)
            validateEmpty(errors, "basicProductModel.boxesPerPalette", "파레트입수", boxesPerPalette)

            if (basicProductCategory == null) return
            validateEmpty(
                errors,
                "basicProductModel.basicProductCategory.level1",
                "상품카테고리(대분류)",
                basicProductCategory.level1
            )
            validateEmpty(
                errors,
                "basicProductModel.basicProductCategory.level2",
                "상품카테고리(중분류)",
                basicProductCategory.level2
            )
        }

        validateEmpty(errors, "subsidiaryMaterialModels", "부자재(매핑)정보", subsidiaryMaterialModels)
        if (subsidiaryMaterialModels.isNotEmpty()) {
            validateCollection(
                saveState,
                errors,
                "subsidiaryMaterialModels",
                subsidiaryMaterialModels.map { it.subsidiaryMaterial },
                basicProductCreateModelValidator
            )
        }
    }

    private fun checkRequiredFieldsCustomSubType(target: BasicProductDetailCreateModel, errors: Errors) {
        val basicProductModel = target.basicProductModel

        if (basicProductModel.type != BasicProductType.CUSTOM_SUB) return

        with(basicProductModel) {
            validateEmpty(errors, "basicProductModel.partnerId", "화주(고객사) ID", partnerId)
            validateEmpty(errors, "basicProductModel.name", "상품명", name)
            validateEmpty(errors, "basicProductModel.barcodeYn", "상품바코드기재여부", barcodeYn)
            validateEmpty(errors, "basicProductModel.subsidiaryMaterialCategory", "부자재카테고리", subsidiaryMaterialCategory)
            validateEmpty(errors, "basicProductModel.piecesPerBox", "박스입수", piecesPerBox)
            validateEmpty(errors, "basicProductModel.boxesPerPalette", "파레트입수", boxesPerPalette)
        }
    }
}
