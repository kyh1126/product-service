package com.smartfoodnet.common.error

import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.error.exception.CreateModelValidateError
import com.smartfoodnet.common.error.exception.ErrorCode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.transaction.TransactionSystemException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException

@RestControllerAdvice
class DefaultExceptionHandler {
    val serviceCode = "FN-PRODUCT"

    @Autowired
    lateinit var environment: Environment

    @ExceptionHandler(value = [MissingServletRequestParameterException::class])
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
    ): ExceptionResponse {
        ex.printStackTrace()
        val prefix = "필수파라미터가 없습니다. "
        return ExceptionResponse(
            serviceCode = serviceCode,
            errorCode = ErrorCode.USER_BAD_REQUEST_DEFAULT.code,
            errorMessage = prefix + ex.message
        )
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
    ): ExceptionResponse {
        ex.printStackTrace()
        val messages =
            ex.allErrors
                .map { "${it.codes?.get(0)?.split(".")?.last()}: ${it.defaultMessage}" }
        return ExceptionResponse(
            serviceCode = serviceCode,
            errorCode = ErrorCode.VALIDATE_ERROR.code,
            errorMessage = messages.joinToString()
        )
    }

    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
    ): ExceptionResponse {
        ex.printStackTrace()
        val prefix = "요청 메세지를 읽을수 없습니다. "
        return ExceptionResponse(
            serviceCode = serviceCode,
            errorCode = ErrorCode.USER_BAD_REQUEST_DEFAULT.code,
            errorMessage = prefix + ex.message

        )
    }

    @ExceptionHandler(value = [DataIntegrityViolationException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolationException(
        request: HttpServletRequest, response: HttpServletResponse,
        ex: DataIntegrityViolationException,
    ): ExceptionResponse {
        ex.printStackTrace()
        return ExceptionResponse(
            serviceCode = serviceCode,
            errorCode = ErrorCode.USER_BAD_REQUEST_DEFAULT.code,
            errorMessage = ex.rootCause?.message ?: ex.localizedMessage
        )
    }

    @ExceptionHandler(value = [MethodArgumentTypeMismatchException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentTypeMismatchException(
        ex: MethodArgumentTypeMismatchException,
    ): ExceptionResponse {
        ex.printStackTrace()
        val prefix = "요청 타입이 다릅니다. "
        return ExceptionResponse(
            serviceCode = serviceCode,
            errorCode = ErrorCode.USER_BAD_REQUEST_DEFAULT.code,
            errorMessage = prefix + "${ex.name}:${ex.value} >> ${ex.requiredType} 타입이 아닙니다"
        )
    }

    @ExceptionHandler(value = [JpaObjectRetrievalFailureException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleJpaObjectRetrievalFailureException(
        ex: JpaObjectRetrievalFailureException,
    ): ExceptionResponse {
        ex.printStackTrace()
        return ExceptionResponse(
            serviceCode = serviceCode,
            errorCode = ErrorCode.USER_BAD_REQUEST_DEFAULT.code,
            errorMessage = "요청한 데이터를 읽을수 없습니다. 삭제되었거나 없는 데이터입니다. ${ex.message}"
        )
    }

    @ExceptionHandler(value = [CreateModelValidateError::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleCreateModelValidateError(
        ex: CreateModelValidateError,
    ): ExceptionResponse {
        ex.printStackTrace()
        return ExceptionResponse(
            serviceCode = serviceCode,
            errorCode = ErrorCode.USER_BAD_REQUEST_DEFAULT.code,
            errorMessage = ex.message ?: ""
        )
    }

    @ExceptionHandler(value = [NoSuchElementException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoSuchElementException(ex: NoSuchElementException): ExceptionResponse {
        ex.printStackTrace()
        return ExceptionResponse(
            serviceCode = serviceCode,
            errorCode = ErrorCode.NO_ELEMENT.code,
            errorMessage = "요청 리소스를 찾을수 없습니다. " + ex.message
        )
    }

    @ExceptionHandler(value = [BaseRuntimeException::class])
    fun handleBaseRuntimeException(ex: BaseRuntimeException): ResponseEntity<out Any> {
        ex.printStackTrace()
        return ResponseEntity<ExceptionResponse>(
            ExceptionResponse(
                serviceCode = serviceCode,
                errorCode = ex.errorCode.code,
                errorMessage = ex.errorMessage ?: ex.errorCode.errorMessage
            ),
            ex.httpStatus
        )
    }

    @ExceptionHandler(value = [ConstraintViolationException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolationException(ex: ConstraintViolationException): ExceptionResponse {
        return ExceptionResponse(
            serviceCode = serviceCode,
            errorCode = ErrorCode.USER_BAD_REQUEST_DEFAULT.code,
            errorMessage = ex.message ?: ErrorCode.USER_BAD_REQUEST_DEFAULT.errorMessage
        )
    }

    @ExceptionHandler(value = [TransactionSystemException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleTransactionSystemException(ex: TransactionSystemException): ExceptionResponse {
        return ExceptionResponse(
            serviceCode = serviceCode,
            errorCode = ErrorCode.USER_BAD_REQUEST_DEFAULT.code,
            errorMessage = ex.cause?.cause?.message ?: ErrorCode.USER_BAD_REQUEST_DEFAULT.errorMessage
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleAllException(e: Exception, request: WebRequest): ExceptionResponse {
        e.printStackTrace()

        return ExceptionResponse(
            serviceCode = serviceCode,
            errorCode = ErrorCode.INTERNAL_SERVER_ERROR.code,
            errorMessage = ErrorCode.INTERNAL_SERVER_ERROR.errorMessage
        )
    }
}
