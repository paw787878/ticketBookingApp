package com.example.ticketbookingapp.dataTransferObject

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
