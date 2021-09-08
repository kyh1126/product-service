package com.smartfoodnet.common.error

data class ExceptionResponse(
    val serviceCode: String,
    val errorCode: Int,
    val errorMessage: String,
)
