package com.example.ticketbookingapp.Exceptions

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.transaction.TransactionSystemException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class CustomGlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ClientResponseException::class)
    fun customHandleNotFound(ex : ClientResponseException, request: WebRequest): ResponseEntity<ApiError> {
        return ResponseEntity(ApiError(ex.responseStatus, ex.message), ex.responseStatus);
    }

    @ExceptionHandler(TransactionSystemException::class)
    fun customHandleNotFound(ex : TransactionSystemException, request: WebRequest): ResponseEntity<ApiError> {
        (ex.cause?.cause as? ConstraintViolationException)?.let { constraintViolationException ->
            val message = constraintViolationException.constraintViolations.joinToString(separator = "\n") { violation ->
                "${violation.invalidValue.toString()} should satisfy ${violation.message}"
            }
            val httpStatus = HttpStatus.BAD_REQUEST
            return ResponseEntity(ApiError(httpStatus, message), httpStatus);
        }

        val serverError = HttpStatus.INTERNAL_SERVER_ERROR
        return ResponseEntity(ApiError(serverError, "Database transaction exception"), serverError);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException::class)
    fun customHandleNotFound(ex : ObjectOptimisticLockingFailureException, request: WebRequest): ResponseEntity<ApiError> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        return ResponseEntity(ApiError(status, "Data on which this request was working was changed. Try again"), status)
    }
}
