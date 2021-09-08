package com.smartfoodnet.common.error.exception

import org.springframework.http.HttpStatus

class UserRequestError(
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    errorCode: ErrorCode = ErrorCode.USER_BAD_REQUEST_DEFAULT,
    errorMessage: String = "Request parameter validation check error",
) : BaseRuntimeException(httpStatus, errorCode, errorMessage)
