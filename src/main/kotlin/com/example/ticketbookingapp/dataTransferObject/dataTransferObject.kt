package com.example.ticketbookingapp.dataTransferObject

import org.springframework.format.annotation.DateTimeFormat
import java.time.Instant

data class ScreeningSelectionDTO(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val minimalStartTime: Instant,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val maximalEndTime: Instant,
)
