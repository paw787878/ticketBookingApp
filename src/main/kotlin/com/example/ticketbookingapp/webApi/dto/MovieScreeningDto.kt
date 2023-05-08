package com.example.ticketbookingapp.webApi.dto

import java.time.Instant

data class MovieScreeningDto(
    val id: Long,
    val title: String,
    val starts: Instant,
    val ends: Instant,
    )
