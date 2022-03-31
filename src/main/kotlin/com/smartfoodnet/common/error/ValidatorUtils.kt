package com.smartfoodnet.common.error

import com.smartfoodnet.common.error.exception.CreateModelValidateError
import org.springframework.validation.BeanPropertyBindingResult

object ValidatorUtils {
    fun <T : Any> validateAndThrow(createModelValidator: CreateModelValidator<T>, target: T) {
        val bindingResult = BeanPropertyBindingResult(target, target::class.java.simpleName)
        createModelValidator.validate(target, bindingResult)

        if (bindingResult.hasErrors()) {
            throw CreateModelValidateError(bindingResult)
        }
    }

    fun <T : Any> validateAndThrow(
        saveState: SaveState,
        createModelValidator: CreateModelValidator<T>,
        target: T
    ) {
        val bindingResult = BeanPropertyBindingResult(target, target::class.java.simpleName)
        createModelValidator.validate(saveState, target, bindingResult)

        if (bindingResult.hasErrors()) {
            throw CreateModelValidateError(bindingResult)
        }
    }
}
