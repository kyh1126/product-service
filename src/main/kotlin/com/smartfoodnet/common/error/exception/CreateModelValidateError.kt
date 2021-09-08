package com.smartfoodnet.common.error.exception

import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.validation.Errors

class CreateModelValidateError(val errors: Errors?) : RuntimeException(errors?.objectName) {
    private val validationErrorMessageSeparator = "\n\t"

    override val message: String?
        get() {
            if (errors == null) {
                return super.message
            }

            return "CreateModelValidateErrorMessage: " +
                    errors.allErrors.map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .joinToString(
                            validationErrorMessageSeparator,
                            validationErrorMessageSeparator,
                            validationErrorMessageSeparator
                        )
        }
}
