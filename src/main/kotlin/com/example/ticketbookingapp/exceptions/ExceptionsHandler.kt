package com.example.ticketbookingapp.exceptions

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class CustomGlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ClientResponseException::class)
    fun customHandleClientResponseException(
        ex: ClientResponseException,
        request: WebRequest
    ): ResponseEntity<ApiError> {
        return ResponseEntity(ApiError(ex.responseStatus, ex.message), ex.responseStatus)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun customHandleTransactionSystemException(
        ex: ConstraintViolationException,
        request: WebRequest
    ): ResponseEntity<ApiError> {
        val message =
            ex.constraintViolations.joinToString(separator = "\n") { violation ->
                "${violation.invalidValue} should satisfy ${violation.message}"
            }
        val httpStatus = HttpStatus.BAD_REQUEST
        return ResponseEntity(ApiError(httpStatus, message), httpStatus)
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException::class)
    fun customHandleTransactionSystemException(
        ex: ObjectOptimisticLockingFailureException,
        request: WebRequest
    ): ResponseEntity<ApiError> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        return ResponseEntity(ApiError(status, "Data on which this request was working was changed. Try again"), status)
    }
}
