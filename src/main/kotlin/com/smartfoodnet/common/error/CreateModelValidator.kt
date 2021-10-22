package com.smartfoodnet.common.error

import org.springframework.validation.Errors
import org.springframework.validation.Validator

interface CreateModelValidator<T> : Validator {
    override fun supports(clazz: Class<*>): Boolean

    @Suppress("UNCHECKED_CAST")
    override fun validate(target: Any, errors: Errors) {
        target as T
        validate(SaveState.CREATE, target, errors)
    }

    fun validate(saveState: SaveState, target: T, errors: Errors)

    fun <T> validateCollection(
        saveState: SaveState,
        errors: Errors,
        fieldName: String,
        targets: Collection<T>,
        validator: CreateModelValidator<T>,
    ) {
        val validationFunction =
            { target: T -> invokeValidator(saveState, validator, target, errors) }

        var current = 0
        targets.forEach {
            validateNested(
                errors,
                "$fieldName[${current++}]",
                it,
                validationFunction
            )
        }
    }

    fun validateEmpty(errors: Errors, fieldName: String, fieldDescription: String, value: Any?) {
        validateNull(errors, fieldName, fieldDescription, value)

        if (value is String && value.isEmpty()) {
            rejectEmpty(errors, fieldName, fieldDescription)
        }

        if (value is Collection<*> && value.isEmpty()) {
            rejectEmpty(errors, fieldName, fieldDescription)
        }
    }

    fun validateNull(errors: Errors, fieldName: String, fieldDescription: String, value: Any?) {
        if (value == null) {
            rejectNull(errors, fieldName, fieldDescription)
        }
    }

    fun rejectEmpty(errors: Errors, fieldName: String, fieldDescription: String) =
        errors.rejectValue(
            fieldName, "empty", arrayOf(fieldDescription),
            "$fieldDescription 값을 입력해주세요."
        )

    fun rejectNull(errors: Errors, fieldName: String, fieldDescription: String) =
        errors.rejectValue(
            fieldName, "null", arrayOf(fieldDescription),
            "$fieldDescription 값은 null 이 아닌 값을 입력해주세요."
        )

    private fun <T> validateNested(
        errors: Errors,
        path: String,
        target: T,
        validationFunction: (T) -> Unit
    ) {
        errors.pushNestedPath(path)
        validationFunction(target)
        errors.popNestedPath()
    }

    private fun <T> invokeValidator(
        saveState: SaveState,
        validator: CreateModelValidator<T>,
        target: T,
        errors: Errors,
    ) {
        @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
        if (target != null && !validator.supports(target!!::class.java)) {
            throw IllegalArgumentException("${validator::class.java} Validator 는 ${target!!::class.java} 를 지원하지 않습니다.")
        }
        validator.validate(saveState, target, errors)
    }

}

enum class SaveState {
    CREATE, UPDATE
}
