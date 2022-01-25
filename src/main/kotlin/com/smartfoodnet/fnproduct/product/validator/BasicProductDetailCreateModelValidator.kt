package com.smartfoodnet.fnproduct.product.validator

import com.smartfoodnet.common.error.CreateModelValidator
import com.smartfoodnet.common.error.SaveState
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.product.model.request.BasicProductCreateModel
import com.smartfoodnet.fnproduct.product.model.request.BasicProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.request.ExpirationDateInfoCreateModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Errors

@Component
@Transactional(readOnly = true)
class BasicProductDetailCreateModelValidator(
    private val basicProductRepository: BasicProductRepository
) : CreateModelValidator<BasicProductDetailCreateModel> {
    override fun supports(clazz: Class<*>): Boolean =
        clazz.isAssignableFrom(BasicProductDetailCreateModel::class.java)

    override fun validate(
        saveState: SaveState,
        target: BasicProductDetailCreateModel,
        errors: Errors
    ) {
        val basicProductModel = target.basicProductModel
        if (checkType(basicProductModel, errors)) return

        when (basicProductModel.type) {
            BasicProductType.BASIC -> checkRequiredFieldsBasicType(target, errors)
            BasicProductType.CUSTOM_SUB -> checkRequiredFieldsCustomSubType(target, errors)
            else -> Unit
        }

        checkDuplicateName(saveState, basicProductModel, errors)

        checkBarcode(saveState, basicProductModel, errors)

        checkExpirationDateInfo(basicProductModel, errors)

        checkTemperatureType(basicProductModel, errors)
    }

    private fun checkType(target: BasicProductCreateModel, errors: Errors): Boolean {
        val isPackageType = target.type == BasicProductType.PACKAGE
        if (isPackageType) {
            errors.rejectValue(
                "basicProductModel.type", "type.invalid", "모음상품은 불가합니다."
            )
        }
        return isPackageType
    }

    private fun checkDuplicateName(
        saveState: SaveState,
        target: BasicProductCreateModel,
        errors: Errors
    ) {
        with(target) {
            if (name == null || partnerId == null) return

            basicProductRepository.findByPartnerIdAndName(partnerId!!, name!!)?.let {
                if (saveState == SaveState.UPDATE && it.id == id) return

                errors.rejectValue(
                    "basicProductModel.name",
                    "name.duplicate",
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
                validateNull(errors, "basicProductModel.partnerId", "화주(고객사) ID", partnerId)

                if (barcode == null || partnerId == null) return

                if (barcode!!.toLongOrNull() == null) {
                    errors.rejectValue(
                        "basicProductModel.barcode",
                        "barcode.invalid",
                        "숫자 입력만 가능합니다."
                    )
                }
                basicProductRepository.findByPartnerIdAndBarcode(partnerId!!, barcode!!)?.let {
                    errors.rejectValue(
                        "basicProductModel.barcode",
                        "barcode.duplicate",
                        "사용중인 상품바코드가 있습니다."
                    )
                }
            }
        }
    }

    private fun checkExpirationDateInfo(target: BasicProductCreateModel, errors: Errors) {
        with(target) {
            if (expirationDateManagementYn.isNotEmpty() && expirationDateManagementYn == "Y") {
                with(expirationDateInfoModel!!) {
                    validateEmpty(
                        errors,
                        "basicProductModel.expirationDateInfoModel.manufactureDateWriteYn",
                        "제조일자기재여부",
                        manufactureDateWriteYn
                    )
                    validateEmpty(
                        errors,
                        "basicProductModel.expirationDateInfoModel.expirationDateWriteYn",
                        "유통기한기재여부",
                        expirationDateWriteYn
                    )

                    checkByExpirationDateWriteYn(this, errors)
                }
            }
        }
    }

    private fun checkByExpirationDateWriteYn(
        expirationDateInfoModel: ExpirationDateInfoCreateModel,
        errors: Errors
    ) {
        with(expirationDateInfoModel) {
            when (expirationDateWriteYn) {
                "Y" -> {
                    if (manufactureDateWriteYn == "N") {
                        validateNull(
                            errors,
                            "basicProductModel.expirationDateInfoModel.expirationDate",
                            "유통기한(제조일+X일)",
                            expirationDate
                        )
                    }
                }
                "N" -> {
                    if (manufactureDateWriteYn == "N" || expirationDate == null) {
                        errors.reject(
                            "basicProductModel.expirationDateInfoModel",
                            "유통기한기재여부가 'N' 이면 제조일자기재여부는 'Y', 유통기한(제조일+X일)도 값이 있어야 합니다."
                        )
                    }
                }
                else -> Unit
            }
        }
    }

    private fun checkTemperatureType(target: BasicProductCreateModel, errors: Errors) {
        with(target) {
            if (handlingTemperature == null) return

            if (handlingTemperature == HandlingTemperatureType.MIX) {
                errors.reject("basicProductModel", "취급온도-혼합은 모음상품 구분만 선택 가능합니다.")
            }
        }
    }

    private fun checkRequiredFieldsBasicType(
        target: BasicProductDetailCreateModel,
        errors: Errors,
    ) {
        val basicProductModel = target.basicProductModel
        val subsidiaryMaterialMappingModels = target.subsidiaryMaterialMappingModels

        with(basicProductModel) {
            if (type != BasicProductType.BASIC) return

            validateNull(errors, "basicProductModel.partnerId", "화주(고객사) ID", partnerId)
            validateEmpty(errors, "basicProductModel.partnerCode", "화주(고객사) 코드", partnerCode)
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
                "basicProductModel.basicProductCategoryId",
                "상품카테고리 ID",
                basicProductCategoryId
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
            validateEmpty(errors, "basicProductModel.piecesPerPalette", "파레트입수", piecesPerPalette)
        }

        validateEmpty(
            errors,
            "basicProductModel.subsidiaryMaterialMappingModels",
            "부자재매핑정보",
            subsidiaryMaterialMappingModels
        )
    }

    private fun checkRequiredFieldsCustomSubType(
        target: BasicProductDetailCreateModel,
        errors: Errors
    ) {
        val basicProductModel = target.basicProductModel

        with(basicProductModel) {
            if (type != BasicProductType.CUSTOM_SUB) return

            validateNull(errors, "basicProductModel.partnerId", "화주(고객사) ID", partnerId)
            validateEmpty(errors, "basicProductModel.partnerCode", "화주(고객사) 코드", partnerCode)
            validateEmpty(errors, "basicProductModel.name", "상품명", name)
            validateEmpty(errors, "basicProductModel.barcodeYn", "상품바코드기재여부", barcodeYn)
            validateEmpty(errors, "basicProductModel.piecesPerBox", "박스입수", piecesPerBox)
            validateEmpty(errors, "basicProductModel.piecesPerPalette", "파레트입수", piecesPerPalette)
        }
    }
}
