package com.example.ticketbookingapp.domain

import jakarta.persistence.*
import jakarta.validation.Valid
import java.time.Instant

@Entity
class Reservation(
    @get:Valid
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val movieScreening: MovieScreening,

    @Column(nullable = false)
    val expirationDate: Instant,

    @Id @GeneratedValue
    val id: Long = 0,
)
