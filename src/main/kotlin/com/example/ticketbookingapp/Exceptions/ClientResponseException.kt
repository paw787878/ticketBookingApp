package com.example.ticketbookingapp.Exceptions

import org.springframework.http.HttpStatus

abstract class ClientResponseException() : RuntimeException() {
    abstract val responseStatus: HttpStatus
    abstract fun messageImplementation(): String

    override val message: String
        get() = messageImplementation()
}

class ClientResponseEntityIdIsWrong(val id: Long, val entityType: Class<*>) : ClientResponseException() {
    override val responseStatus: HttpStatus
        get() = HttpStatus.BAD_REQUEST

    override fun messageImplementation(): String {
        return "$id is wrong id for ${entityType.simpleName}"
    }
}
