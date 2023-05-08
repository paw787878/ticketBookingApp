package com.example.ticketbookingapp.webApi.createReservation.dto

import java.math.BigDecimal
import java.time.Instant

data class ReservationResponseDto(
    val totalAmountToPay: BigDecimal,
    val reservationExpirationTime: Instant,
)
