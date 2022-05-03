package com.smartfoodnet.fnproduct.release.validator

import com.smartfoodnet.common.error.CreateModelValidator
import com.smartfoodnet.common.error.SaveState
import com.smartfoodnet.fnproduct.release.model.request.ManualOrderModel
import org.springframework.stereotype.Component
import org.springframework.validation.Errors

@Component
class ManualOrderModelValidator : CreateModelValidator<ManualOrderModel> {
    override fun supports(clazz: Class<*>): Boolean =
        clazz.isAssignableFrom(ManualOrderModel::class.java)

    override fun validate(saveState: SaveState, target: ManualOrderModel, errors: Errors) {
        validateEmpty(errors, "receiverName", "받는사람 이름", target.receiverName)
        validateEmpty(errors, "receiverPhoneNumber", "받는사람 전화번호", target.receiverPhoneNumber)
        validateEmpty(errors, "receiverAddress", "받는사람 주소", target.receiverAddress)

        checkEmptyProducts(errors, target)
    }

    private fun checkEmptyProducts(errors: Errors, target: ManualOrderModel) {
        validateEmpty(errors, "products", "발주할 기본상품", target.products)
    }
}
