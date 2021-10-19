package com.smartfoodnet.common.error

data class ExceptionResponse(
    val serviceCode: String,
    val errorCode: String,
    val errorMessage: String,
)
