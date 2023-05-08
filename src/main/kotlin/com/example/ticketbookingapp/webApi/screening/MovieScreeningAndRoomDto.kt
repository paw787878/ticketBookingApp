package com.example.ticketbookingapp.webApi.screening

data class MovieScreeningAndRoomDto(
    val movieScreening: MovieScreeningDto,
    val roomName: String,
    val seats: List<SeatDto>,
)
