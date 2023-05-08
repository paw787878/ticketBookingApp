package com.example.ticketbookingapp.webApi.screening

data class SeatDto(
    val rowName: String,
    val columnName: String,
    val id: Long,
    val available: Boolean,
)
