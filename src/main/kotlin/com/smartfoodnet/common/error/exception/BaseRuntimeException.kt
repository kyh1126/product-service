package com.smartfoodnet.common.error.exception

import org.springframework.http.HttpStatus

open class BaseRuntimeException(
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    val errorCode: ErrorCode = ErrorCode.USER_BAD_REQUEST_DEFAULT,
    val errorMessage: String? = null,
) : RuntimeException()

enum class ErrorCode(val value: Int, val code: String, val errorMessage: String) {
    USER_BAD_REQUEST_DEFAULT(1, "E001", "잘못된 호출입니다."),
    JWT_TOKEN_ERROR(2, "E002", "잘못된 토큰입니다."),
    VALIDATE_ERROR(3, "E003", "유효하지 않은 값입니다."),
    NO_ELEMENT(4, "E004", "아이템이 존재하지 않습니다."),
    NOT_CHANGE(5, "E005", "변경사항이 없습니다."),
    INTERNAL_SERVER_ERROR(6, "E006", "알 수 없는 에러가 발생했습니다. 관리자에게 문의해 주세요."),
}
