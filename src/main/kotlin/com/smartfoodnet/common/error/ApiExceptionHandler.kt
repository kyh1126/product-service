package com.smartfoodnet.common.error

import com.fasterxml.jackson.databind.ObjectMapper
import com.smartfoodnet.common.error.exception.ErrorCode
import com.smartfoodnet.common.utils.Log
import feign.FeignException
import feign.RetryableException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException

@RestControllerAdvice
class ApiExceptionHandler(
    val objectMapper : ObjectMapper
) : Log {
    val serviceCode = "FN-PRODUCT"

    @ExceptionHandler(value = [FeignException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleFeignException(ex : FeignException) : ExceptionResponse{
        ex.printStackTrace()
        val res = objectMapper.readValue(ex.responseBody().get().array() , ExceptionResponse::class.java)
        return ExceptionResponse(
            serviceCode = "$serviceCode, ${res.serviceCode}",
            errorCode = ex.status().toString(),
            errorMessage = "${res.errorCode}-${res.errorMessage}"
        )
    }

    @ExceptionHandler(value = [RetryableException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleFeignRetryableException(ex: RetryableException) : ExceptionResponse{
        return ExceptionResponse(
            serviceCode = serviceCode,
            errorCode = ErrorCode.INTERNAL_SERVER_ERROR.code,
            errorMessage = ex.message?:ErrorCode.INTERNAL_SERVER_ERROR.errorMessage)
    }


    @ExceptionHandler(value = [HttpClientErrorException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpClientErrorException(ex : HttpClientErrorException) : ExceptionResponse{
        val res = objectMapper.readValue(ex.responseBodyAsString, ExceptionResponse::class.java)

        return ExceptionResponse(
            serviceCode = "$serviceCode, ${res.serviceCode}",
            errorCode = ex.statusCode.value().toString(),
            errorMessage = "${res.errorCode}-${res.errorMessage}"
        )
    }

    @ExceptionHandler(value = [HttpServerErrorException::class])
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleHttpServerErrorException(ex : HttpServerErrorException) : ExceptionResponse{
        val res = objectMapper.readValue(ex.responseBodyAsString, ExceptionResponse::class.java)

        return ExceptionResponse(
            serviceCode = serviceCode,
            errorCode = ex.statusCode.value().toString(),
            errorMessage = "${res.errorCode}-${res.errorMessage}"
        )
    }
}
