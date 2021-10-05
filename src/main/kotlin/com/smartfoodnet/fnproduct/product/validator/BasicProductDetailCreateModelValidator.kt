package com.smartfoodnet.fnproduct.product.validator

import com.smartfoodnet.common.error.CreateModelValidator
import com.smartfoodnet.common.error.SaveState
import com.smartfoodnet.fnproduct.product.model.request.BasicProductDetailCreateModel
import org.springframework.stereotype.Component
import org.springframework.validation.Errors

@Component
class BasicProductDetailCreateModelValidator(
) : CreateModelValidator<BasicProductDetailCreateModel> {
    override fun supports(clazz: Class<*>): Boolean = clazz.isAssignableFrom(BasicProductDetailCreateModel::class.java)

    override fun validate(saveState: SaveState, target: BasicProductDetailCreateModel, errors: Errors) {
        // TODO: 유효성 검사 작성중
    }

}
