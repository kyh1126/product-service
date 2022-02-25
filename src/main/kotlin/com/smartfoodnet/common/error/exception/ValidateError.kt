package com.smartfoodnet.common.error.exception

import org.springframework.http.HttpStatus

class ValidateError(
    errorMessage: String = "Request parameter validation check error",
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    errorCode: ErrorCode = ErrorCode.VALIDATE_ERROR,
) : BaseRuntimeException(httpStatus, errorCode, errorMessage)
