package com.example.ticketbookingapp.webApi.screenings.dto

import com.example.ticketbookingapp.webApi.dto.MovieScreeningDto

data class ScreeningSelectionResponseDto(
    val elements: List<MovieScreeningDto>,
    val totalElementsNumber: Int,
)
