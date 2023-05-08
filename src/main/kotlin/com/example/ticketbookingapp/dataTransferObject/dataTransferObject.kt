package com.example.ticketbookingapp.dataTransferObject

import java.math.BigDecimal
import java.time.Instant

data class ReservationRequestDto(
    val movieScreeningId: Long,
    val name: String,
    val surname: String,
    val seats: List<SeatTicketTypeDto>,
)

data class SeatTicketTypeDto(
    val seatId: Long,
    val ticketType: Long,
)

data class ReservationResponseDto(
    val totalAmountToPay: BigDecimal,
    val reservationExpirationTime: Instant,
)
