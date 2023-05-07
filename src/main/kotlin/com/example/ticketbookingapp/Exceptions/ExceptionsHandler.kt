package com.example.ticketbookingapp.Exceptions

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
            return ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
        // TODO_PAWEL also other probably like optimistic

        return ResponseEntity("Database transaction exception", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
