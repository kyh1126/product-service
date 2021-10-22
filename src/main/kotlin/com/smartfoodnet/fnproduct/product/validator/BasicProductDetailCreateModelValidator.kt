package com.smartfoodnet.fnproduct.product.validator

import com.smartfoodnet.common.error.CreateModelValidator
import com.smartfoodnet.common.error.SaveState
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.product.model.request.BasicProductCreateModel
import com.smartfoodnet.fnproduct.product.model.request.BasicProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Errors

@Component
@Transactional(readOnly = true)
class BasicProductDetailCreateModelValidator(
    private val basicProductRepository: BasicProductRepository,
    private val basicProductCreateModelValidator: BasicProductCreateModelValidator,
) : CreateModelValidator<BasicProductDetailCreateModel> {
    override fun supports(clazz: Class<*>): Boolean =
        clazz.isAssignableFrom(BasicProductDetailCreateModel::class.java)

    override fun validate(
        saveState: SaveState,
        target: BasicProductDetailCreateModel,
        errors: Errors
    ) {
        val basicProductModel = target.basicProductModel
        when (basicProductModel.type) {
            BasicProductType.BASIC -> checkRequiredFieldsBasicType(saveState, target, errors)
            BasicProductType.CUSTOM_SUB -> checkRequiredFieldsCustomSubType(target, errors)
            else -> Unit
        }

        checkDuplicateName(basicProductModel, errors)

        checkBarcode(saveState, basicProductModel, errors)

        checkExpirationDate(basicProductModel, errors)
    }

    private fun checkDuplicateName(target: BasicProductCreateModel, errors: Errors) {
        with(target) {
            if (name == null || partnerId == null) return

            basicProductRepository.findByPartnerIdAndName(partnerId, name)?.let {
                errors.rejectValue(
                    "basicProductModel.name",
                    "barcode.duplicate",
                    "사용중인 상품명이 있습니다. 기본상품코드: $code"
                )
            }
        }
    }

    private fun checkBarcode(
        saveState: SaveState,
        target: BasicProductCreateModel,
        errors: Errors
    ) {
        if (saveState == SaveState.UPDATE) return

        with(target) {
            if (barcodeYn.isNotEmpty() && barcodeYn == "Y") {
                validateEmpty(errors, "basicProductModel.barcode", "상품바코드", barcode)
                validateEmpty(errors, "basicProductModel.partnerId", "화주(고객사) ID", partnerId)

                if (barcode == null || partnerId == null) return

                if (barcode.toLongOrNull() == null) {
                    errors.rejectValue(
                        "basicProductModel.barcode",
                        "barcode.invalid",
                        "숫자 입력만 가능합니다."
                    )
                }
                basicProductRepository.findByPartnerIdAndBarcode(partnerId, barcode)?.let {
                    errors.rejectValue(
                        "basicProductModel.barcode",
                        "barcode.invalid",
                        "사용중인 상품바코드가 있습니다."
                    )
                }
            }
        }
    }

    private fun checkExpirationDate(target: BasicProductCreateModel, errors: Errors) {
        with(target) {
            if (expirationDateManagementYn.isNotEmpty() && expirationDateManagementYn == "Y") {
                validateEmpty(
                    errors,
                    "basicProductModel.expirationDateInfoModel",
                    "유통기한정보",
                    expirationDateInfoModel
                )

                if (expirationDateInfoModel == null) return
                with(expirationDateInfoModel) {
                    if (manufactureDateWriteYn == "N" && expirationDateWriteYn == "N") {
                        errors.reject(
                            "basicProductModel.expirationDateInfoModel",
                            "유통기한정보의 유통기한 기재 여부, 제조일자 기재 여부 중 하나는 YES 여야 합니다."
                        )
                    }
                    if (expirationDateWriteYn == "Y") {
                        validateNull(
                            errors,
                            "basicProductModel.expirationDateInfoModel.expirationDate",
                            "유통기한(제조일+X일)",
                            expirationDate
                        )
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

        with(basicProductModel) {
            if (type != BasicProductType.BASIC) return

            validateEmpty(errors, "basicProductModel.partnerId", "화주(고객사) ID", partnerId)
            validateEmpty(errors, "basicProductModel.name", "상품명", name)
            validateEmpty(errors, "basicProductModel.barcodeYn", "상품바코드기재여부", barcodeYn)
            validateEmpty(
                errors,
                "basicProductModel.handlingTemperature",
                "취급온도",
                handlingTemperature
            )
            validateEmpty(
                errors,
                "basicProductModel.basicProductCategory",
                "상품카테고리",
                basicProductCategory
            )
            validateEmpty(
                errors,
                "basicProductModel.singlePackagingYn",
                "단수(포장)여부",
                singlePackagingYn
            )
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

    private fun checkRequiredFieldsCustomSubType(
        target: BasicProductDetailCreateModel,
        errors: Errors
    ) {
        val basicProductModel = target.basicProductModel

        with(basicProductModel) {
            if (type != BasicProductType.CUSTOM_SUB) return

            validateEmpty(errors, "basicProductModel.partnerId", "화주(고객사) ID", partnerId)
            validateEmpty(errors, "basicProductModel.name", "상품명", name)
            validateEmpty(errors, "basicProductModel.barcodeYn", "상품바코드기재여부", barcodeYn)
            validateEmpty(errors, "basicProductModel.piecesPerBox", "박스입수", piecesPerBox)
            validateEmpty(errors, "basicProductModel.boxesPerPalette", "파레트입수", boxesPerPalette)
        }
    }
}
