package com.example.ticketbookingapp.webApi.screening.dto

import com.example.ticketbookingapp.webApi.dto.MovieScreeningDto

data class MovieScreeningAndRoomDto(
    val movieScreening: MovieScreeningDto,
    val roomName: String,
    val seats: List<SeatDto>,
)
