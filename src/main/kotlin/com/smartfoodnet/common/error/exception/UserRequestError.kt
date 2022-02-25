package com.smartfoodnet.common.error.exception

import org.springframework.http.HttpStatus

class UserRequestError(
    errorMessage: String = "Request parameter validation check error",
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    errorCode: ErrorCode = ErrorCode.USER_BAD_REQUEST_DEFAULT,
) : BaseRuntimeException(httpStatus, errorCode, errorMessage)
