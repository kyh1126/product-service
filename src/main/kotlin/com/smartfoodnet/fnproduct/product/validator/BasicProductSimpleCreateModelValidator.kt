package com.smartfoodnet.fnproduct.product.validator

import com.smartfoodnet.common.error.CreateModelValidator
import com.smartfoodnet.common.error.SaveState
import com.smartfoodnet.fnproduct.product.model.request.BasicProductSimpleCreateModel
import org.springframework.stereotype.Component
import org.springframework.validation.Errors

@Component
class BasicProductSimpleCreateModelValidator : CreateModelValidator<BasicProductSimpleCreateModel> {
    override fun supports(clazz: Class<*>): Boolean =
        clazz.isAssignableFrom(BasicProductSimpleCreateModel::class.java)

    override fun validate(
        saveState: SaveState,
        target: BasicProductSimpleCreateModel,
        errors: Errors
    ) {
        checkRequiredFields(target, errors)
    }

    private fun checkRequiredFields(target: BasicProductSimpleCreateModel, errors: Errors) {
        validateNull(errors, "id", "id", target.id)
    }
}
