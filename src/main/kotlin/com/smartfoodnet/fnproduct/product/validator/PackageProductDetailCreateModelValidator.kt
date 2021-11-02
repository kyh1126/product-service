package com.smartfoodnet.fnproduct.product.validator

import com.smartfoodnet.common.error.CreateModelValidator
import com.smartfoodnet.common.error.SaveState
import com.smartfoodnet.fnproduct.product.model.request.PackageProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import org.springframework.stereotype.Component
import org.springframework.validation.Errors

@Component
class PackageProductDetailCreateModelValidator(
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
                    "basicProductModel.type",
                    "type.invalid",
                    "모음상품 타입만(PACKAGE) 가능합니다."
                )
            }

            validateNull(errors, "basicProductModel.partnerId", "화주(고객사) ID", partnerId)
            validateEmpty(errors, "basicProductModel.name", "상품명", name)
            validateEmpty(errors, "basicProductModel.barcodeYn", "상품바코드기재여부", barcodeYn)
            validateEmpty(
                errors,
                "basicProductModel.handlingTemperature",
                "취급온도",
                handlingTemperature
            )
        }

        validateEmpty(errors, "packageProductModels", "모음상품매핑정보", packageProductMappingModels)
        if (packageProductMappingModels.isNotEmpty()) {
            validateCollection(
                saveState,
                errors,
                "packageProductModels",
                packageProductMappingModels.map { it.basicProductModel },
                basicProductSimpleCreateModelValidator
            )
        }
    }
}
