package com.example.ticketbookingapp.Exceptions

import org.springframework.http.HttpStatus

abstract class ClientResponseException() : RuntimeException() {
    abstract val responseStatus: HttpStatus
    abstract fun messageImplementation(): String

    override val message: String
        get() = messageImplementation()
}
