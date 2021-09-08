package com.smartfoodnet.common.error.exception

import org.springframework.http.HttpStatus

class NotChangeError(
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    errorCode: ErrorCode = ErrorCode.NOT_CHANGE,
    errorMessage: String = "변경사항이 없습니다. ",
) : BaseRuntimeException(httpStatus, errorCode, errorMessage)
