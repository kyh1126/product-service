package com.smartfoodnet.fnproduct.release.validator

import com.smartfoodnet.common.error.CreateModelValidator
import com.smartfoodnet.common.error.SaveState
import com.smartfoodnet.fnproduct.release.model.ManualReleaseCreateModel
import org.springframework.stereotype.Component
import org.springframework.validation.Errors

@Component
class ManualReleaseCreateModelValidator
    : CreateModelValidator<ManualReleaseCreateModel> {

    override fun supports(clazz: Class<*>): Boolean =
        clazz.isAssignableFrom(ManualReleaseCreateModel::class.java)

    override fun validate(saveState: SaveState, target: ManualReleaseCreateModel, errors: Errors) {
        validateEmpty(errors, "receiverName", "받는사람 이름", target.receiverName)
        validateEmpty(errors, "receiverPhoneNumber", "받는사람 전화번호", target.receiverPhoneNumber)
        validateEmpty(errors, "receiverAddress", "받는사람 주소", target.receiverAddress)

        checkEmptyProducts(errors, target)
    }

    private fun checkEmptyProducts(errors: Errors, target: ManualReleaseCreateModel) {
        validateEmpty(errors, "products", "발주할 기본상품", target.products)
    }
}