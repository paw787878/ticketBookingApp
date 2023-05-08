package com.example.ticketbookingapp.domain

import jakarta.persistence.*
import jakarta.validation.constraints.Size

@Entity
class Seat(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val screeningRoom: ScreeningRoom,

    @Column(nullable = false)
    @get:Size(max = 255)
    val rowName: String,

    @Column(nullable = false)
    @get:Size(max = 255)
    val columnName: String,

    @Id @GeneratedValue
    val id: Long = 0,
){
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    var seatToLeft: Seat? = null
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    var seatToRight: Seat? = null

    init {
        screeningRoom.seats.add(this)
    }
}
