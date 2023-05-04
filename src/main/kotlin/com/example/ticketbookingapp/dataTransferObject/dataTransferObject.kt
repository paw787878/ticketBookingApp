package com.example.ticketbookingapp.dataTransferObject

import com.example.ticketbookingapp.domain.Movie
import com.example.ticketbookingapp.domain.ScreeningRoom
import com.example.ticketbookingapp.domain.Seat
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.format.annotation.DateTimeFormat
import java.math.BigDecimal
import java.time.Instant

data class ScreeningSelectionRequestDto(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val minimalStartTime: Instant,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val maximalEndTime: Instant,
    val offset: Int,
    val limit: Int,
)

data class ScreeningInfoRequestDto(
    val screeningId: Long,
)

data class MovieScreeningDto(
    val id: Long,
    val title: String,
    val starts: Instant,
    val ends: Instant,
    )

data class ScreeningSelectionResponseDto(
    val elements: List<MovieScreeningDto>,
    val totalElementsNumber: Long,
)

data class SeatDto(
    val rowName: String,
    val columnName: String,
    val id: Long,
    val available: Boolean,
)

data class MovieScreeningAndRoomDto(
    val movieScreening: MovieScreeningDto,
    val roomName: String,
    val seats: List<SeatDto>,
)

// TODO_PAWEL maybe debug info, like what time it is and what delay to use
open class ReservationRequestDto(
    val movieScreeningId: Long,
    val name: String,
    val surname: String,
    val seats: List<Long>,
)

data class ReservationResponseDto(
    // TODO_PAWEL is it good for numbers?
    val totalAmountToPay: BigDecimal,
    val reservationExpirationTime: Instant,
)
