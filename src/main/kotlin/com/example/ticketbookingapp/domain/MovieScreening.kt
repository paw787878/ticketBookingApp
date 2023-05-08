package com.example.ticketbookingapp.domain

import jakarta.persistence.*
import java.time.Instant

@Entity
class MovieScreening(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val movie: Movie,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val screeningRoom: ScreeningRoom,

    @Column(nullable = false)
    val timeOfStart: Instant,

    @Column(nullable = false)
    val timeOfEnd: Instant,

    @Id @GeneratedValue
    val id: Long = 0,
    @Version
    val version: Int = 0,
)
