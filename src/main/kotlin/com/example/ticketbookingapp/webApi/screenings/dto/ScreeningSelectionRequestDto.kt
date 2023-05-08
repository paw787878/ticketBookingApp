package com.example.ticketbookingapp.webApi.screenings.dto

import org.springframework.format.annotation.DateTimeFormat
import java.time.Instant

// TODO_PAWEL can they be like private?
data class ScreeningSelectionRequestDto(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val minimalStartTime: Instant,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val maximalEndTime: Instant,
    val offset: Int,
    val limit: Int,
)
