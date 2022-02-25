package com.smartfoodnet.common.error.exception

import org.springframework.http.HttpStatus

class NotChangeError(
    errorMessage: String = "변경사항이 없습니다. ",
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    errorCode: ErrorCode = ErrorCode.NOT_CHANGE,
) : BaseRuntimeException(httpStatus, errorCode, errorMessage)
