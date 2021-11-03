package com.smartfoodnet.fnproduct.product.validator

import com.smartfoodnet.common.error.CreateModelValidator
import com.smartfoodnet.common.error.SaveState
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.product.model.request.BasicProductSimpleCreateModel
import com.smartfoodnet.fnproduct.product.model.request.PackageProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.request.PackageProductMappingCreateModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import org.springframework.stereotype.Component
import org.springframework.validation.Errors

@Component
class PackageProductDetailCreateModelValidator(
    private val basicProductRepository: BasicProductRepository,
    private val basicProductSimpleCreateModelValidator: BasicProductSimpleCreateModelValidator
) : CreateModelValidator<PackageProductDetailCreateModel> {
    override fun supports(clazz: Class<*>): Boolean =
        clazz.isAssignableFrom(PackageProductDetailCreateModel::class.java)

    override fun validate(
        saveState: SaveState,
        target: PackageProductDetailCreateModel,
        errors: Errors
    ) {
        checkRequiredFields(saveState, target, errors)

        checkDuplicateName(saveState, target.packageProductModel, errors)

        checkBarcode(saveState, target.packageProductModel, errors)

        checkActiveYn(target.packageProductMappingModels, errors)
    }

    private fun checkDuplicateName(
        saveState: SaveState,
        target: BasicProductSimpleCreateModel,
        errors: Errors
    ) {
        with(target) {
            if (name == null || partnerId == null) return

            basicProductRepository.findByPartnerIdAndName(partnerId, name)?.let {
                if (saveState == SaveState.UPDATE && it.id == id) return

                errors.rejectValue(
                    "packageProductModel.name",
                    "name.duplicate",
                    "사용중인 상품명이 있습니다. 기본상품코드: $code"
                )
            }
        }
    }

    private fun checkBarcode(
        saveState: SaveState,
        target: BasicProductSimpleCreateModel,
        errors: Errors
    ) {
        if (saveState == SaveState.UPDATE) return

        with(target) {
            if ((barcodeYn.isNotEmpty() && barcodeYn == "Y")) {
                errors.rejectValue(
                    "packageProductModel.barcodeYn",
                    "barcodeYn.invalid",
                    "모음상품은 상품바코드기재여부가 'N' 이어야 합니다."
                )
            }

            if (barcode != null) {
                errors.rejectValue(
                    "packageProductModel.barcode",
                    "barcode.invalid",
                    "모음상품은 바코드 입력이 불가합니다."
                )
            }
        }
    }

    private fun checkActiveYn(target: List<PackageProductMappingCreateModel>, errors: Errors) {
        val inactivatedBasicProduct =
            target.map { it.basicProductModel }.find { it.activeYn == "N" }
        if (inactivatedBasicProduct != null) {
            errors.rejectValue(
                "packageProductMappingModels",
                "activeYn.invalid",
                "모음상품의 기본상품 중 활성여부가 'N'인 것이 있습니다. 기본상품 id: ${inactivatedBasicProduct.id}"
            )
        }
    }

    private fun checkRequiredFields(
        saveState: SaveState,
        target: PackageProductDetailCreateModel,
        errors: Errors,
    ) {
        val packageProductModel = target.packageProductModel
        val packageProductMappingModels = target.packageProductMappingModels

        with(packageProductModel) {
            if (type != BasicProductType.PACKAGE) {
                errors.rejectValue(
                    "packageProductModel.type",
                    "type.invalid",
                    "모음상품 타입만(PACKAGE) 가능합니다."
                )
            }

            validateNull(errors, "packageProductModel.partnerId", "화주(고객사) ID", partnerId)
            validateEmpty(errors, "packageProductModel.name", "상품명", name)
            validateEmpty(errors, "packageProductModel.barcodeYn", "상품바코드기재여부", barcodeYn)
            validateEmpty(
                errors,
                "packageProductModel.handlingTemperature",
                "취급온도",
                handlingTemperature
            )
        }

        validateEmpty(
            errors,
            "packageProductMappingModels",
            "모음상품매핑정보",
            packageProductMappingModels
        )
        if (packageProductMappingModels.isNotEmpty()) {
            validateCollection(
                saveState,
                errors,
                "packageProductMappingModels",
                packageProductMappingModels.map { it.basicProductModel },
                basicProductSimpleCreateModelValidator
            )
        }
    }
}
