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
    fun customHandleNotFound(ex : ClientResponseException, request: WebRequest): ResponseEntity<String> {

        return ResponseEntity(ex.message, ex.responseStatus);
    }

    @ExceptionHandler(TransactionSystemException::class)
    fun customHandleNotFound(ex : TransactionSystemException, request: WebRequest): ResponseEntity<String> {
        (ex.cause?.cause as? ConstraintViolationException)?.let { constraintViolationException ->
            val message = constraintViolationException.constraintViolations.joinToString(separator = "\n") { violation ->
                "${violation.invalidValue.toString()} should satisfy ${violation.message}"
            }
            // TODO_PAWEL add test for this
            return ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }

        // TODO_PAWEL return json with http status in field
        return ResponseEntity("Database transaction exception", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException::class)
    fun customHandleNotFound(ex : ObjectOptimisticLockingFailureException, request: WebRequest): ResponseEntity<String> {
        return ResponseEntity("Data on which this request was working was changed. Try again", HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
