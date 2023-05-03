package com.example.ticketbookingapp.dataTransferObject

import com.example.ticketbookingapp.domain.MovieScreening
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

data class ScreeningSelectionResponseDto(
    val elements: List<MovieScreening>,
    val totalElementsNumber: Long,
)
