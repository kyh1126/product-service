package com.smartfoodnet.fnproduct.product.validator

import com.smartfoodnet.common.error.CreateModelValidator
import com.smartfoodnet.common.error.SaveState
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.product.model.request.BasicProductPackageCreateModel
import com.smartfoodnet.fnproduct.product.model.request.PackageProductDetailCreateModel
import com.smartfoodnet.fnproduct.product.model.request.PackageProductMappingCreateModel
import org.springframework.stereotype.Component
import org.springframework.validation.Errors

@Component
class PackageProductDetailCreateModelValidator(
    private val basicProductRepository: BasicProductRepository
) : CreateModelValidator<PackageProductDetailCreateModel> {
    override fun supports(clazz: Class<*>): Boolean =
        clazz.isAssignableFrom(PackageProductDetailCreateModel::class.java)

    override fun validate(
        saveState: SaveState,
        target: PackageProductDetailCreateModel,
        errors: Errors
    ) {
        checkRequiredFields(target, errors)

        checkDuplicateName(saveState, target.packageProductModel, errors)

        checkActiveYn(target.packageProductMappingModels, errors)
    }

    private fun checkDuplicateName(
        saveState: SaveState,
        target: BasicProductPackageCreateModel,
        errors: Errors
    ) {
        with(target) {
            if (name == null || partnerId == null) return

            basicProductRepository.findByPartnerIdAndName(partnerId, name)?.let {
                if (saveState == SaveState.UPDATE && it.id == id) return

                errors.rejectValue(
                    "packageProductModel.name",
                    "name.duplicate",
                    "사용중인 상품명이 있습니다. 기본상품코드: ${it.code}"
                )
            }
        }
    }

    private fun checkActiveYn(target: List<PackageProductMappingCreateModel>, errors: Errors) {
        val inactivatedBasicProducts =
            basicProductRepository.findAllById(target.map { it.basicProductId })
                .filter { it.activeYn == "N" }

        if (inactivatedBasicProducts.isNotEmpty()) {
            val inactivatedIds = inactivatedBasicProducts.map { it.id }.joinToString(",")
            errors.rejectValue(
                "packageProductMappingModels",
                "activeYn.invalid",
                "모음상품의 기본상품 중 활성여부가 'N'인 것이 있습니다. 기본상품 id: ${inactivatedIds}"
            )
        }
    }

    private fun checkRequiredFields(
        target: PackageProductDetailCreateModel,
        errors: Errors,
    ) {
        val packageProductMappingModels = target.packageProductMappingModels

        validateEmpty(
            errors,
            "packageProductMappingModels",
            "모음상품매핑정보",
            packageProductMappingModels
        )
    }
}
