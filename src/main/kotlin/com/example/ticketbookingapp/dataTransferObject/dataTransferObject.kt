package com.example.ticketbookingapp.dataTransferObject

import org.springframework.format.annotation.DateTimeFormat
import java.util.Date

data class ScreeningSelectionDTO(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val minimalStartTime: Date,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val maximalEndTime: Date,
)
