package com.smartfoodnet.common.error.exception

import org.springframework.http.HttpStatus

class ExternalApiError(
    errorMessage: String = "외부 api 연동 에러가 발생하였습니다.",
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    errorCode: ErrorCode = ErrorCode.NO_ELEMENT,
) : BaseRuntimeException(httpStatus, errorCode, errorMessage)
