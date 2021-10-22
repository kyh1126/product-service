package com.smartfoodnet.fnproduct.product.validator

import com.smartfoodnet.common.error.CreateModelValidator
import com.smartfoodnet.common.error.SaveState
import com.smartfoodnet.fnproduct.product.model.request.BasicProductCreateModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import org.springframework.stereotype.Component
import org.springframework.validation.Errors

@Component
class BasicProductCreateModelValidator : CreateModelValidator<BasicProductCreateModel> {
    override fun supports(clazz: Class<*>): Boolean =
        clazz.isAssignableFrom(BasicProductCreateModel::class.java)

    override fun validate(saveState: SaveState, target: BasicProductCreateModel, errors: Errors) {
        checkRequiredFields(target, errors)
    }

    private fun checkRequiredFields(target: BasicProductCreateModel, errors: Errors) {
        when (target.type) {
            BasicProductType.SUB, BasicProductType.CUSTOM_SUB -> validateNull(
                errors,
                "id",
                "id",
                target.id
            )
            else -> Unit
        }
    }
}
