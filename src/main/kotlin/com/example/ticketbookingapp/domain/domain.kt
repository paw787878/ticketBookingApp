package com.example.ticketbookingapp.domain

import jakarta.persistence.*
import java.util.Date

@Embeddable
class User(
    var name: String,
    var surname: String,
)

@Entity
class Movie(
    val title: String,
    @Id @GeneratedValue
    val id: Long = 0,
)

@Entity
class ScreeningRoom(
    val name: String,
    @Id @GeneratedValue
    val id: Long = 0,
)

@Entity
class MovieScreening(
    @ManyToOne(fetch = FetchType.LAZY)
    val movie: Movie,
    @ManyToOne(fetch = FetchType.LAZY)
    val screeningRoom: ScreeningRoom,
    val timeOfStart: Date,
    val timeOfEnd: Date,
    @Id @GeneratedValue
    val id: Long = 0,
)

@Entity
class Reservation(
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
    val movieScreening: MovieScreening,
    @ManyToMany
    val seats: Set<Seat>,
    val expirationDate: Date,
    @Id @GeneratedValue
    val id: Long = 0,
)

@Entity
class Seat(
    @ManyToOne(fetch = FetchType.LAZY)
    val screeningRoom: ScreeningRoom,
    val name: String,
    @Id @GeneratedValue
    val id: Long = 0,
){
    @ManyToOne(fetch = FetchType.LAZY)
    var seatToLeft: Seat? = null
    @ManyToOne(fetch = FetchType.LAZY)
    var seatToRight: Seat? = null
}
