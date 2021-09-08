package com.smartfoodnet.common.error.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST, reason = "request parameter validation error")
class JwtTokenError(
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    errorCode: ErrorCode = ErrorCode.JWT_TOKEN_ERROR,
    errorMessage: String = "token validate error",
) : BaseRuntimeException(httpStatus, errorCode, errorMessage)
