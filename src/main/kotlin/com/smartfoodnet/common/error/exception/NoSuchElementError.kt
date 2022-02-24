package com.smartfoodnet.common.error.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST, reason = "requested element does not exist")
class NoSuchElementError(
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    errorCode: ErrorCode = ErrorCode.NO_ELEMENT,
    errorMessage: String = "The element requested does not exist",
) : BaseRuntimeException(httpStatus, errorCode, errorMessage)
