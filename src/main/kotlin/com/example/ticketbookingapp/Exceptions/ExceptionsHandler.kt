package com.example.ticketbookingapp.Exceptions

import org.springframework.http.ResponseEntity
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
}
