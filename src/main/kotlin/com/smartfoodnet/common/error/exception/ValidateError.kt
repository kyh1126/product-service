package com.smartfoodnet.common.error.exception

import org.springframework.http.HttpStatus

class ValidateError(
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    errorCode: ErrorCode = ErrorCode.VALIDATE_ERROR,
    errorMessage: String = "Request parameter validation check error",
) : BaseRuntimeException(httpStatus, errorCode, errorMessage)
