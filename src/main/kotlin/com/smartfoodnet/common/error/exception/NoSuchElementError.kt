package com.smartfoodnet.common.error.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST, reason = "requested element does not exist")
class NoSuchElementError(
    errorMessage: String = "The element requested does not exist",
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    errorCode: ErrorCode = ErrorCode.NO_ELEMENT
) : BaseRuntimeException(httpStatus, errorCode, errorMessage)
