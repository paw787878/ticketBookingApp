package com.example.ticketbookingapp.webApi.screenings

import com.example.ticketbookingapp.webApi.screening.MovieScreeningDto

data class ScreeningSelectionResponseDto(
    val elements: List<MovieScreeningDto>,
    val totalElementsNumber: Long,
)
