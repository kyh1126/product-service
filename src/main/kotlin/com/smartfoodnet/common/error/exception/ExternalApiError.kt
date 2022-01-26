package com.smartfoodnet.common.error.exception

import org.springframework.http.HttpStatus

class ExternalApiError(
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    errorCode: ErrorCode = ErrorCode.NO_ELEMENT,
    errorMessage: String = "외부 api 연동 에러가 발생하였습니다.",
) : BaseRuntimeException(httpStatus, errorCode, errorMessage)
