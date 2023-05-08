package com.example.ticketbookingapp.webApi.createReservation.dto

data class ReservationRequestDto(
    val movieScreeningId: Long,
    val name: String,
    val surname: String,
    val seats: List<SeatTicketTypeDto>,
)
