package com.example.ticketbookingapp.Exceptions

import org.springframework.http.HttpStatus

data class ApiError(
    val status: HttpStatus,
    val message: String,
)
