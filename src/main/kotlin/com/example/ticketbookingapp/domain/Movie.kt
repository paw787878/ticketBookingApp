package com.example.ticketbookingapp.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.validation.constraints.Size

@Entity
class Movie(
    @Column(nullable = false)
    @get:Size(max = 255)
    val title: String,

    @Column(nullable = false)
    val lengthInMinutes: Int,

    @Id @GeneratedValue
    val id: Long = 0,
)
