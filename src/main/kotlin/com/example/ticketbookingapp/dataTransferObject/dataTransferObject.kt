package com.example.ticketbookingapp.dataTransferObject

import com.example.ticketbookingapp.domain.Movie
import com.example.ticketbookingapp.domain.ScreeningRoom
import org.springframework.format.annotation.DateTimeFormat
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
