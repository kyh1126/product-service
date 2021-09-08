package com.smartfoodnet.common.error.exception

import org.springframework.http.HttpStatus

open class BaseRuntimeException(
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    val errorCode: ErrorCode = ErrorCode.USER_BAD_REQUEST_DEFAULT,
    val errorMessage: String,
) : RuntimeException()

enum class ErrorCode(val value: Int) {
    USER_BAD_REQUEST_DEFAULT(1),
    JWT_TOKEN_ERROR(2),
    VALIDATE_ERROR(3),
    NO_ELEMENT(4),
    NOT_CHANGE(5),
}
